package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.repository.ScheduledCommunicationRepository;

@Service
public class ScheduledCommunicationService {

    private final ScheduledCommunicationRepository repo;

    public ScheduledCommunicationService(ScheduledCommunicationRepository repo) {
        this.repo = repo;
    }

    public List<ScheduledCommunication> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public ScheduledCommunication save(ScheduledCommunication event) {
        return repo.save(event);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
