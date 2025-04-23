package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.repository.CommunicationLogRepository;

@Service
public class CommunicationLogService {

    private final CommunicationLogRepository repo;

    public CommunicationLogService(CommunicationLogRepository repo) {
        this.repo = repo;
    }

    public List<CommunicationLog> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId);
    }

    public CommunicationLog save(CommunicationLog log) {
        return repo.save(log);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
