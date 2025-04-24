package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;

@Component
public class AuditLogMapper {

    public AuditLogDTO toDTO(AuditLog log) {
        return new AuditLogDTO(
            log.getId(),
            log.getAction().name(),
            log.getDescription(),
            log.getCreatedAt(),
            log.getUser().getId()
        );
    }

    public AuditLog toEntity(AuditLogDTO dto) {
        AuditLog log = new AuditLog();
        log.setAction(AuditLog.Action.valueOf(dto.action));
        log.setDescription(dto.description);
        log.setCreatedAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now());

        User user = new User();
        user.setId(dto.userId);
        log.setUser(user);

        return log;
    }
}
