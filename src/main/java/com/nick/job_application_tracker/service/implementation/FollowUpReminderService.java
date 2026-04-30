package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.response.FollowUpReminderResponseDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.FollowUpReminderMapper;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class FollowUpReminderService {

    private final FollowUpReminderRepository repo;
    private final FollowUpReminderMapper mapper;
    private final AuditLogService auditLogService;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @Autowired
    public FollowUpReminderService(
        FollowUpReminderRepository repo,
        FollowUpReminderMapper mapper,
        AuditLogService auditLogService,
        JobApplicationRepository jobApplicationRepository,
        UserRepository userRepository
    ) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    public FollowUpReminderService(
        FollowUpReminderRepository repo,
        FollowUpReminderMapper mapper,
        AuditLogService auditLogService,
        JobApplicationRepository jobApplicationRepository
    ) {
        this(repo, mapper, auditLogService, jobApplicationRepository, null);
    }

    public List<FollowUpReminderResponseDTO> getByJobAppId(UUID jobAppId) {
        JobApplication jobApplication = getAccessibleJobApplication(jobAppId);
        return repo.findByJobApplicationIdAndDeletedFalse(jobApplication.getId(), Pageable.unpaged()).getContent().stream()
            .map(mapper::toResponseDTO)
            .toList();
    }

    public FollowUpReminderResponseDTO create(FollowUpReminderCreateDTO dto) {
        JobApplication application = getAccessibleJobApplication(dto.getJobApplicationId());

        FollowUpReminder reminder = mapper.toEntity(dto, application);
        FollowUpReminder saved = repo.save(reminder);

        auditLogService.logCreate("Created follow-up reminder with id: " + saved.getId());

        return mapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        FollowUpReminder reminder = getAccessibleReminder(id);
        reminder.softDelete();
        repo.save(reminder);
        auditLogService.logDelete("Deleted follow-up reminder with id: " + id);
    }

    private JobApplication getAccessibleJobApplication(UUID jobApplicationId) {
        if (userRepository == null) {
            return jobApplicationRepository.findById(jobApplicationId)
                .filter(jobApplication -> !jobApplication.isDeleted())
                .orElseThrow(() -> new NotFoundException("Job application not found", null));
        }

        User user = SecurityUtils.getCurrentUserOrThrow(userRepository);
        return jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(jobApplicationId, user.getId())
            .orElseThrow(() -> new NotFoundException("Job application not found", null));
    }

    private FollowUpReminder getAccessibleReminder(UUID id) {
        if (userRepository == null) {
            return repo.findById(id)
                .filter(reminder -> !reminder.isDeleted())
                .orElseThrow(() -> new NotFoundException("Follow-up reminder not found", null));
        }

        User user = SecurityUtils.getCurrentUserOrThrow(userRepository);
        return repo.findByIdAndJobApplicationUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(() -> new NotFoundException("Follow-up reminder not found", null));
    }
}
