package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.SkillTrackerRepository;

@Service
public class SkillTrackerService {

    private final SkillTrackerRepository repo;
    private final AuditLogService auditLogService;

    public SkillTrackerService(SkillTrackerRepository repo, AuditLogService auditLogService) {
        this.repo = repo;
        this.auditLogService = auditLogService;
    }

    public List<SkillTracker> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public SkillTracker save(SkillTracker skill) {
        SkillTracker savedSkill = repo.save(skill);
        auditLogService.logCreate("Created SkillTracker with ID " + savedSkill.getId() + " for JobApplication ID " + savedSkill.getJobApplication().getId());
        return savedSkill;
    }

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted SkillTracker with ID " + id);
    }
}
