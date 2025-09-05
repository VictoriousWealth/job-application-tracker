package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AuditLogRepositoryCustom {

    Page<AuditLog> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable);

    long countByUserAndAction(UUID userId, AuditLog.Action action);

    Page<AuditLog> searchByDescription(UUID userId, String keyword, Pageable pageable);
}
