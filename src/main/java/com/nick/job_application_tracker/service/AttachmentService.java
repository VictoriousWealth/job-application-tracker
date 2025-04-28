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
    private final AuditLogService auditLogService;

    public AttachmentService(AttachmentRepository repository, AttachmentMapper mapper, AuditLogService auditLogService) {
        this.repository = repository;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<AttachmentDTO> getByJobAppId(Long jobAppId) {
        return repository.findByJobApplicationId(jobAppId).stream()
            .map(mapper::toDTO)
            .toList();
    }

    public AttachmentDTO save(AttachmentDTO dto) {
        Attachment attachment = mapper.toEntity(dto);
        AttachmentDTO savedDto = mapper.toDTO(repository.save(attachment));
        
        auditLogService.logCreate("Created Attachment with ID " + savedDto.getId());
        
        return savedDto;
    }

    public void delete(Long id) {
        repository.deleteById(id);
        auditLogService.logDelete("Deleted Attachment with ID " + id);
    }
}
