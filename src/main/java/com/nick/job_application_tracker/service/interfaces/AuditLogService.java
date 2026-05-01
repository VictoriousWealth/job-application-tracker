package com.nick.job_application_tracker.service.interfaces;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {

    void log(AuditLog.Action action, String description);

    default void logCreate(String description) {
        log(AuditLog.Action.CREATE, description);
    }

    default void logUpdate(String description) {
        log(AuditLog.Action.UPDATE, description);
    }

    default void logDelete(String description) {
        log(AuditLog.Action.DELETE, description);
    }

    List<AuditLogDTO> findAll();

    default List<AuditLogDTO> getAll() {
        return findAll();
    }

    AuditLogDTO getById(UUID id);

    AuditLogDTO save(AuditLogDTO dto);
}
