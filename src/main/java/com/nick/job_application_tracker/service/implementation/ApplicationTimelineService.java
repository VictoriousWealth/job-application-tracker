package com.nick.job_application_tracker.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.dto.detail.ApplicationTimelineDetailDTO;
import com.nick.job_application_tracker.dto.response.ApplicationTimelineResponseDTO;
import com.nick.job_application_tracker.dto.update.ApplicationTimelineUpdateDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.ApplicationTimelineMapper;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.ApplicationTimeline.EventType;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class ApplicationTimelineService {

    private final ApplicationTimelineRepository repo;
    private final AuditLogService auditLogService;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationTimelineService(
        ApplicationTimelineRepository repo,
        AuditLogService auditLogService,
        JobApplicationRepository jobApplicationRepository,
        UserRepository userRepository
    ) {
        this.repo = repo;
        this.auditLogService = auditLogService;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
    }

    public ApplicationTimelineService(
        ApplicationTimelineRepository repo,
        AuditLogService auditLogService,
        JobApplicationRepository jobApplicationRepository
    ) {
        this(repo, auditLogService, jobApplicationRepository, null);
    }

    public ApplicationTimelineService(
        ApplicationTimelineRepository repo,
        AuditLogService auditLogService
    ) {
        this(repo, auditLogService, null, null);
    }

    public List<ApplicationTimelineResponseDTO> getByJobAppId(UUID jobAppId) {
        JobApplication jobApplication = getAccessibleJobApplication(jobAppId);
        return repo.findByJobApplicationIdAndDeletedFalse(jobApplication.getId(), Pageable.unpaged()).getContent().stream()
            .map(ApplicationTimelineMapper::toResponseDTO)
            .toList();
    }

    public ApplicationTimeline save(ApplicationTimeline entity) {
        boolean isUpdate = entity.getId() != null;
        ApplicationTimeline saved = repo.save(entity);

        if (isUpdate) {
            auditLogService.logUpdate("Updated ApplicationTimeline event (ID: " + saved.getId() + ")");
        } else {
            auditLogService.logCreate("Created a new ApplicationTimeline event (ID: " + saved.getId() + ")");
        }

        return saved;
    }

    public void delete(UUID id) {
        ApplicationTimeline entity = getAccessibleTimeline(id);
        entity.softDelete();
        repo.save(entity);
        auditLogService.logDelete("Deleted an ApplicationTimeline event (ID: " + id + ")");
    }

    public ApplicationTimelineDetailDTO create(ApplicationTimelineCreateDTO dto) {
        JobApplication jobApplication = getAccessibleJobApplication(dto.getJobApplicationId());
        ApplicationTimeline entity = ApplicationTimelineMapper.toEntity(dto, jobApplication);
        ApplicationTimeline savedEntity = save(entity);
        return ApplicationTimelineMapper.toDetailDTO(savedEntity);
    }

    public ApplicationTimelineDetailDTO update(UUID id, ApplicationTimelineUpdateDTO dto) {
        ApplicationTimeline existing = getAccessibleTimeline(id);
        ApplicationTimelineMapper.updateEntityWithDTOInfo(existing, dto);
        return ApplicationTimelineMapper.toDetailDTO(save(existing));
    }

    public ApplicationTimelineDetailDTO patch(UUID id, Map<String, Object> updates) {
        ApplicationTimeline existing = getAccessibleTimeline(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "eventType" -> existing.setEventType(EventType.from((String) value));
                case "eventTime" -> existing.setEventTime(LocalDateTime.parse((String) value));
                case "description" -> existing.setDescription((String) value);
                default -> {}
            }
        });

        return ApplicationTimelineMapper.toDetailDTO(save(existing));
    }

    private JobApplication getAccessibleJobApplication(UUID jobApplicationId) {
        if (jobApplicationRepository == null) {
            JobApplication jobApplication = new JobApplication();
            jobApplication.setId(jobApplicationId);
            return jobApplication;
        }

        if (userRepository == null) {
            return jobApplicationRepository.findById(jobApplicationId)
                .filter(jobApplication -> !jobApplication.isDeleted())
                .orElseThrow(() -> new NotFoundException("Job application not found", null));
        }

        User user = getCurrentUser();
        return jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(jobApplicationId, user.getId())
            .orElseThrow(() -> new NotFoundException("Job application not found", null));
    }

    private ApplicationTimeline getAccessibleTimeline(UUID id) {
        if (userRepository == null) {
            return repo.findById(id)
                .filter(timeline -> !timeline.isDeleted())
                .orElseThrow(() -> new NotFoundException("Timeline event with ID " + id + " not found", null));
        }

        User user = getCurrentUser();
        return repo.findByIdAndJobApplicationUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(() -> new NotFoundException("Timeline event with ID " + id + " not found", null));
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUserOrThrow(userRepository);
    }
}
