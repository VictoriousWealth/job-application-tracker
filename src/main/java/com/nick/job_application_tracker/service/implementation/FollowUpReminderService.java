package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.response.FollowUpReminderResponseDTO;
import com.nick.job_application_tracker.mapper.FollowUpReminderMapper;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.inter_face.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

@Service
public class FollowUpReminderService {

    private final FollowUpReminderRepository repo;
    private final FollowUpReminderMapper mapper;
    private final AuditLogService auditLogService;
    private final JobApplicationRepository jobApplicationRepository;

    public FollowUpReminderService(
        FollowUpReminderRepository repo,
        FollowUpReminderMapper mapper,
        AuditLogService auditLogService,
        JobApplicationRepository jobApplicationRepository
    ) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public List<FollowUpReminderResponseDTO> getByJobAppId(UUID jobAppId) {
        return repo.findByJobApplicationIdAndDeletedFalse(jobAppId, Pageable.unpaged()).getContent().stream()
            .map(mapper::toResponseDTO)
            .toList();
    }

    public FollowUpReminderResponseDTO create(FollowUpReminderCreateDTO dto) {
        JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new IllegalArgumentException("Job Application not found"));

        FollowUpReminder reminder = mapper.toEntity(dto, application);
        FollowUpReminder saved = repo.save(reminder);

        auditLogService.logCreate("Created follow-up reminder with id: " + saved.getId());

        return mapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted follow-up reminder with id: " + id);
    }

}
