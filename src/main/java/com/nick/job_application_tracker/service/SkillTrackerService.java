package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.SkillTrackerRepository;

@Service
public class SkillTrackerService {

    private final SkillTrackerRepository repo;

    public SkillTrackerService(SkillTrackerRepository repo) {
        this.repo = repo;
    }

    public List<SkillTracker> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public SkillTracker save(SkillTracker skill) {
        return repo.save(skill);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
