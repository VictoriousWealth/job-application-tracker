package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.AttachmentCreateDTO;
import com.nick.job_application_tracker.dto.response.AttachmentResponseDTO;
import com.nick.job_application_tracker.mapper.AttachmentMapper;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.AttachmentRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

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

    public AttachmentResponseDTO create(AttachmentCreateDTO dto) {
        var jobApplication = jobAppRepo.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new EntityNotFoundException("Job Application with ID " + dto.getJobApplicationId() + " not found"));
        Attachment attachment = AttachmentMapper.toEntity(dto, jobApplication);
        AttachmentResponseDTO savedDto = AttachmentMapper.toResponseDTO(repository.save(attachment));
        auditLogService.logCreate("Created Attachment with ID " + savedDto.getId());
        return savedDto;
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Attachment with ID " + id + " not found");
        }
        repository.deleteById(id);
        auditLogService.logDelete("Deleted Attachment with ID " + id);
    }

    public List<AttachmentResponseDTO> getByJobAppId(UUID jobAppId) {
        return repository.findByJobApplicationIdAndDeletedFalse(jobAppId).stream()
            .map(AttachmentMapper::toResponseDTO)
            .toList();
    }
}
