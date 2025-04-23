package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;

    public AuditLogService(AuditLogRepository repo) {
        this.repo = repo;
    }

    public List<AuditLog> findAll() {
        return repo.findAll();
    }

    public AuditLog save(AuditLog audit) {
        return repo.save(audit);
    }
}
