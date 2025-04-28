package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.mapper.ApplicationTimelineMapper;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.repository.ApplicationTimelineRepository;

@Service
public class ApplicationTimelineService {

    private final ApplicationTimelineRepository repo;
    private final AuditLogService auditLogService;

    public ApplicationTimelineService(ApplicationTimelineRepository repo, AuditLogService auditLogService) {
        this.repo = repo;
        this.auditLogService = auditLogService;
    }

    public List<ApplicationTimelineDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
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

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted an ApplicationTimeline event (ID: " + id + ")");
    }

    public ApplicationTimelineDTO save(ApplicationTimelineDTO dto) {
        ApplicationTimeline entity = ApplicationTimelineMapper.toEntity(dto);
        ApplicationTimeline savedEntity = save(entity); // ✨ REUSE
        return ApplicationTimelineMapper.toDTO(savedEntity);
    }
    
}
