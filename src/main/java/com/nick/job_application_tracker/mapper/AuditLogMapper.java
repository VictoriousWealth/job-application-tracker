package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
            log.getPerformedBy().getId()
        );
    }

    public AuditLog toEntity(AuditLogDTO dto, User currentUser) {
        AuditLog log = new AuditLog();
        try {
            log.setAction(AuditLog.Action.valueOf(dto.action));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action type");
        }

        log.setDescription(dto.description);
        log.setCreatedAt(LocalDateTime.now()); // always generated on the server
        log.setPerformedBy(currentUser);              // authenticated user

        return log;
    }

}
