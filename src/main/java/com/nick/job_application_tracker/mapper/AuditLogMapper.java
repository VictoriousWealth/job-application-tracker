package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.detail.AuditLogDetailDTO;
import com.nick.job_application_tracker.dto.response.AuditLogResponseDTO;
import com.nick.job_application_tracker.model.AuditLog;

@Component
public class AuditLogMapper {

    public AuditLogDetailDTO toDTO(AuditLog log) {
        AuditLogDetailDTO dto = new AuditLogDetailDTO();
        dto.setId(log.getId());
        dto.setAction(log.getAction());
        dto.setUserId(log.getPerformedBy() == null ? null : log.getPerformedBy().getId());
        return dto;
    }

    public static AuditLogResponseDTO toResponseDTO(AuditLog auditLog) {
        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(auditLog.getId());
        dto.setAction(auditLog.getAction());
        return dto;
    }

}
