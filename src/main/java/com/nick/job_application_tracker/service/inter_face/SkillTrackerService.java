package com.nick.job_application_tracker.service.inter_face;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.inter_face.SkillTrackerRepository;

@Service
public class SkillTrackerService {

    private final SkillTrackerRepository repo;
    private final AuditLogService auditLogService;

    public SkillTrackerService(SkillTrackerRepository repo, AuditLogService auditLogService) {
        this.repo = repo;
        this.auditLogService = auditLogService;
    }

    public List<SkillTracker> getByJobAppId(UUID jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public SkillTracker save(SkillTracker skill) {
        SkillTracker savedSkill = repo.save(skill);
        auditLogService.logCreate("Created SkillTracker with ID " + savedSkill.getId() + " for JobApplication ID " + savedSkill.getJobApplication().getId());
        return savedSkill;
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted SkillTracker with ID " + id);
    }
}
