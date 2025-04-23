package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.AttachmentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository repository;

    public AttachmentService(AttachmentRepository repo) {
        this.repository = repo;
    }

    public List<AttachmentDTO> getByJobAppId(Long jobAppId) {
        return repository.findByJobApplicationId(jobAppId).stream()
            .map(this::toDTO)
            .toList();
    }

    public AttachmentDTO save(AttachmentDTO dto) {
        Attachment attachment = new Attachment();
        attachment.setType(Attachment.Type.valueOf(dto.type));
        attachment.setFilePath(dto.filePath);

        JobApplication job = new JobApplication();
        job.setId(dto.jobApplicationId);
        attachment.setJobApplication(job);

        return toDTO(repository.save(attachment));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    private AttachmentDTO toDTO(Attachment attachment) {
        return new AttachmentDTO(
            attachment.getId(),
            attachment.getType().name(),
            attachment.getFilePath(),
            attachment.getJobApplication().getId()
        );
    }

}
