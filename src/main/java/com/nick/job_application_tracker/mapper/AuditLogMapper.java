package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.dto.detail.AuditLogDetailDTO;
import com.nick.job_application_tracker.dto.response.AuditLogResponseDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;

@Component
public class AuditLogMapper {

    public AuditLogDTO toDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(log.getId());
        dto.setAction(log.getAction() == null ? null : log.getAction().name());
        dto.setDescription(log.getDescription());
        dto.setCreatedAt(log.getCreatedAt());
        dto.setUserId(log.getPerformedBy() == null ? null : log.getPerformedBy().getId());
        return dto;
    }

    public AuditLogDetailDTO toDetailDTO(AuditLog log) {
        AuditLogDetailDTO dto = new AuditLogDetailDTO();
        dto.setId(log.getId());
        dto.setAction(log.getAction());
        dto.setUserId(log.getPerformedBy() == null ? null : log.getPerformedBy().getId());
        return dto;
    }

    public AuditLog toEntity(AuditLogDTO dto, User performedBy) {
        AuditLog auditLog = new AuditLog();
        if (dto.getId() != null) {
            auditLog.setId(dto.getId());
        }
        try {
            auditLog.setAction(dto.getAction() == null ? null : AuditLog.Action.from(dto.getAction()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid action type: " + dto.getAction(), ex);
        }
        auditLog.setDescription(dto.getDescription());
        auditLog.setPerformedBy(performedBy);
        if (auditLog.getCreatedAt() == null) {
            auditLog.setCreatedAt(java.time.LocalDateTime.now());
        }
        return auditLog;
    }

    public static AuditLogResponseDTO toResponseDTO(AuditLog auditLog) {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(auditLog.getId());
        dto.setAction(auditLog.getAction());
        return dto;
    }

}
