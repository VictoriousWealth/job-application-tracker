package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.mapper.AttachmentMapper;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.repository.AttachmentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository repository;
    private final AttachmentMapper mapper;

    public AttachmentService(AttachmentRepository repository, AttachmentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<AttachmentDTO> getByJobAppId(Long jobAppId) {
        return repository.findByJobApplicationId(jobAppId).stream()
            .map(mapper::toDTO)
            .toList();
    }

    public AttachmentDTO save(AttachmentDTO dto) {
        Attachment attachment = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(attachment));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
