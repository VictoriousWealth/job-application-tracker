package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.AttachmentCreateDTO;
import com.nick.job_application_tracker.dto.response.AttachmentResponseDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.AttachmentMapper;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.AttachmentRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class AttachmentService {

    private final AttachmentRepository repository;
    private final JobApplicationRepository jobAppRepo;
    private final AttachmentMapper mapper;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Autowired
    public AttachmentService(
        AttachmentRepository repository,
        JobApplicationRepository jobAppRepo,
        AttachmentMapper mapper,
        AuditLogService auditLogService,
        UserRepository userRepository
    ) {
        this.repository = repository;
        this.jobAppRepo = jobAppRepo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public AttachmentService(
        AttachmentRepository repository,
        JobApplicationRepository jobAppRepo,
        AttachmentMapper mapper,
        AuditLogService auditLogService
    ) {
        this(repository, jobAppRepo, mapper, auditLogService, null);
    }

    public AttachmentResponseDTO create(AttachmentCreateDTO dto) {
        JobApplication jobApplication = getAccessibleJobApplication(dto.getJobApplicationId());
        Attachment attachment = mapper.toEntity(dto, jobApplication);
        AttachmentResponseDTO savedDto = AttachmentMapper.toResponseDTO(repository.save(attachment));
        auditLogService.logCreate("Created Attachment with ID " + savedDto.getId());
        return savedDto;
    }

    public void delete(UUID id) {
        Attachment attachment = getAccessibleAttachment(id);
        attachment.softDelete();
        repository.save(attachment);
        auditLogService.logDelete("Deleted Attachment with ID " + id);
    }

    public List<AttachmentResponseDTO> getByJobAppId(UUID jobAppId) {
        JobApplication jobApplication = getAccessibleJobApplication(jobAppId);
        return repository.findByJobApplicationIdAndDeletedFalse(jobApplication.getId()).stream()
            .map(AttachmentMapper::toResponseDTO)
            .toList();
    }

    private JobApplication getAccessibleJobApplication(UUID jobApplicationId) {
        if (userRepository == null) {
            return jobAppRepo.findById(jobApplicationId)
                .filter(jobApplication -> !jobApplication.isDeleted())
                .orElseThrow(() -> new NotFoundException("Job application not found", null));
        }

        User user = SecurityUtils.getCurrentUserOrThrow(userRepository);
        return jobAppRepo.findByIdAndUserIdAndDeletedFalse(jobApplicationId, user.getId())
            .orElseThrow(() -> new NotFoundException("Job application not found", null));
    }

    private Attachment getAccessibleAttachment(UUID id) {
        if (userRepository == null) {
            return repository.findById(id)
                .filter(attachment -> !attachment.isDeleted())
                .orElseThrow(() -> new NotFoundException("Attachment not found", null));
        }

        User user = SecurityUtils.getCurrentUserOrThrow(userRepository);
        return repository.findByIdAndJobApplicationUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(() -> new NotFoundException("Attachment not found", null));
    }
}
