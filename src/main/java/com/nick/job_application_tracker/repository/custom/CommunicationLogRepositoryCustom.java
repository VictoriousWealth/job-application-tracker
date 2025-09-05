package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.CommunicationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CommunicationLogRepositoryCustom {

    Page<CommunicationLog> findRecentLogsByUser(UUID userId, LocalDateTime since, Pageable pageable);

    long countByUserAndMethod(UUID userId, CommunicationLog.Method method);

    Page<CommunicationLog> searchByMessage(UUID userId, String keyword, Pageable pageable);
}
