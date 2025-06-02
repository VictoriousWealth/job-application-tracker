package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.mapper.AttachmentMapper;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.repository.AttachmentRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AttachmentService {

    private final AttachmentRepository repository;
    private final JobApplicationRepository jobAppRepo;
    private final AttachmentMapper mapper;
    private final AuditLogService auditLogService;

    public AttachmentService(
        AttachmentRepository repository,
        JobApplicationRepository jobAppRepo,
        AttachmentMapper mapper,
        AuditLogService auditLogService
    ) {
        this.repository = repository;
        this.jobAppRepo = jobAppRepo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public AttachmentDTO save(AttachmentDTO dto) {
        if (!jobAppRepo.existsById(dto.getJobApplicationId())) {
            throw new EntityNotFoundException("Job Application with ID " + dto.getJobApplicationId() + " not found");
        }

        Attachment attachment = mapper.toEntity(dto);
        AttachmentDTO savedDto = mapper.toDTO(repository.save(attachment));
        auditLogService.logCreate("Created Attachment with ID " + savedDto.getId());
        return savedDto;
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Attachment with ID " + id + " not found");
        }
        repository.deleteById(id);
        auditLogService.logDelete("Deleted Attachment with ID " + id);
    }

    public List<AttachmentDTO> getByJobAppId(Long jobAppId) {
        return repository.findByJobApplicationId(jobAppId).stream()
            .map(mapper::toDTO)
            .toList();
    }
}
