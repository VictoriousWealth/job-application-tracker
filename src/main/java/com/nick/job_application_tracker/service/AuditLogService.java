package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.AuditLogRepository;
import com.nick.job_application_tracker.dto.AuditLogDTO;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;

    public AuditLogService(AuditLogRepository repo) {
        this.repo = repo;
    }

    public List<AuditLogDTO> findAll() {
        return repo.findAll().stream()
            .map(this::toDTO)
            .toList();
    }

    public AuditLogDTO save(AuditLogDTO dto) {
        AuditLog log = new AuditLog();
        log.setAction(AuditLog.Action.valueOf(dto.action));
        log.setDescription(dto.description);
        log.setCreatedAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now());

        User user = new User();
        user.setId(dto.userId);
        log.setPerformedBy(user);

        return toDTO(repo.save(log));
    }

    private AuditLogDTO toDTO(AuditLog log) {
        return new AuditLogDTO(
            log.getId(),
            log.getAction().name(),
            log.getDescription(),
            log.getCreatedAt(),
            log.getPerformedBy().getId()
        );
    }

}
