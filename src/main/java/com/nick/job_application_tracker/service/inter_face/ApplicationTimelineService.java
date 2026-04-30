package com.nick.job_application_tracker.service.inter_face;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.ApplicationTimelineMapper;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.ApplicationTimeline.EventType;
import com.nick.job_application_tracker.repository.inter_face.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;

@Service
public class ApplicationTimelineService {

    private final ApplicationTimelineRepository repo;
    private final AuditLogService auditLogService;
    private final JobApplicationRepository jobApplicationRepository;

    public ApplicationTimelineService(
        ApplicationTimelineRepository repo,
        AuditLogService auditLogService,
        JobApplicationRepository jobApplicationRepository
    ) {
        this.repo = repo;
        this.auditLogService = auditLogService;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public List<ApplicationTimelineCreateDTO> getByJobAppId(UUID jobAppId) {
        return repo.findByJobApplicationIdAndDeletedFalse(jobAppId, Pageable.unpaged()).getContent().stream()
            .map(ApplicationTimelineMapper::toDTO)
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
        repo.deleteById(id);
        auditLogService.logDelete("Deleted an ApplicationTimeline event (ID: " + id + ")");
    }

    public ApplicationTimelineCreateDTO save(ApplicationTimelineCreateDTO dto) {
        var jobApplication = jobApplicationRepository.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new NotFoundException("Job application not found", null));
        ApplicationTimeline entity = ApplicationTimelineMapper.toEntity(dto, jobApplication);
        ApplicationTimeline savedEntity = save(entity); // ✨ REUSE
        return ApplicationTimelineMapper.toDTO(savedEntity);
    }
    

    public ApplicationTimelineCreateDTO update(UUID id, ApplicationTimelineCreateDTO dto) {
        ApplicationTimeline existing = repo.findById(id)
            .orElseThrow(() -> new NotFoundException("Timeline event with ID " + id + " not found", null));

        existing.setEventType(dto.getEventType() == null ? null : EventType.from(dto.getEventType()));
        existing.setEventTime(dto.getEventTime());
        existing.setDescription(dto.getDescription());

        return ApplicationTimelineMapper.toDTO(save(existing));
    }

    public ApplicationTimelineCreateDTO patch(UUID id, Map<String, Object> updates) {
        ApplicationTimeline existing = repo.findById(id)
            .orElseThrow(() -> new NotFoundException("Timeline event with ID " + id + " not found", null));

        updates.forEach((key, value) -> {
            switch (key) {
                case "eventType" -> existing.setEventType(EventType.from((String) value));
                case "eventTime" -> existing.setEventTime(LocalDateTime.parse((String) value));
                case "description" -> existing.setDescription((String) value);
                default -> {} // ignore unknown fields
            }
        });

        return ApplicationTimelineMapper.toDTO(save(existing));
    }

}
