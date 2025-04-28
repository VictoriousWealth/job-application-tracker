package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.mapper.AttachmentMapper;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.repository.AttachmentRepository;

@Service
public class AttachmentService {

    private final AttachmentRepository repo;
    private final AttachmentMapper mapper;
    private final AuditLogService auditLogService;

    public AttachmentService(AttachmentRepository repo, AttachmentMapper mapper, AuditLogService auditLogService) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<AttachmentDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public Optional<AttachmentDTO> findById(Long id) {
        return repo.findById(id).map(mapper::toDTO);
    }

    public AttachmentDTO saveAttachment(AttachmentDTO dto) {
        boolean isNew = (dto.getId() == null);

        Attachment saved = repo.save(mapper.toEntity(dto));
        AttachmentDTO savedDto = mapper.toDTO(saved);

        if (isNew) {
            auditLogService.logCreate("Created new attachment for job ID " + saved.getJobApplication().getId());
        } else {
            auditLogService.logUpdate("Updated attachment ID " + saved.getId());
        }

        return savedDto;
    }

    public void deleteAttachment(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted attachment ID " + id);
    }
}
