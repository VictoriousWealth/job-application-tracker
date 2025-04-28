package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.mapper.FollowUpReminderMapper;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;

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

    public List<FollowUpReminderDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(mapper::toDTO)
            .toList();
    }

    public FollowUpReminderDTO save(FollowUpReminderCreateDTO dto) {
        JobApplication application = jobApplicationRepository.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new IllegalArgumentException("Job Application not found"));

        FollowUpReminder reminder = mapper.toEntity(dto, application);
        FollowUpReminder saved = repo.save(reminder);

        auditLogService.logCreate("Created follow-up reminder with id: " + saved.getId());

        return mapper.toDTO(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted follow-up reminder with id: " + id);
    }
}
