package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.AuditLog.Action;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    // Standard access
    Page<AuditLog> findByPerformedByIdAndDeletedFalse(UUID userId, Pageable pageable);

    // Action-based filter
    Page<AuditLog> findByPerformedByIdAndActionAndDeletedFalse(UUID userId, Action action, Pageable pageable);

    // Soft-deleted (recycle bin)
    Page<AuditLog> findByPerformedByIdAndDeletedTrue(UUID userId, Pageable pageable);

    // Drafts
    Page<AuditLog> findByPerformedByIdAndIsDraftTrueAndDeletedFalse(UUID userId, Pageable pageable);
}
