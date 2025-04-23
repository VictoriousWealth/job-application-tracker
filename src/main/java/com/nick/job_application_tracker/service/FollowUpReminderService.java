package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.repository.FollowUpReminderRepository;

@Service
public class FollowUpReminderService {

    private final FollowUpReminderRepository repo;

    public FollowUpReminderService(FollowUpReminderRepository repo) {
        this.repo = repo;
    }

    public List<FollowUpReminder> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public FollowUpReminder save(FollowUpReminder reminder) {
        return repo.save(reminder);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
