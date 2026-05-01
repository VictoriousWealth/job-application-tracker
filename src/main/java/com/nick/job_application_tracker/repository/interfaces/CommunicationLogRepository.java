package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.CommunicationLog.Method;
import com.nick.job_application_tracker.model.CommunicationLog.Direction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommunicationLogRepository
        extends JpaRepository<CommunicationLog, UUID> {

    // Ownership-based
    Page<CommunicationLog> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    // Filters by jobApplicationId
    Page<CommunicationLog> findByJobApplicationIdAndDeletedFalse(UUID jobAppId, Pageable pageable);
    default List<CommunicationLog> findByJobApplicationId(UUID jobAppId) {
        return findByJobApplicationIdAndDeletedFalse(jobAppId, Pageable.unpaged()).getContent();
    }
    Optional<CommunicationLog> findByIdAndJobApplicationUserIdAndDeletedFalse(UUID id, UUID userId);
    Page<CommunicationLog> findByJobApplicationIdAndIsDraftFalseAndDeletedFalse(UUID jobAppId, Pageable pageable);

    // Enum-based filters
    Page<CommunicationLog> findByJobApplicationIdAndTypeAndDeletedFalse(UUID jobAppId, Method method, Pageable pageable);
    Page<CommunicationLog> findByJobApplicationIdAndDirectionAndDeletedFalse(UUID jobAppId, Direction direction, Pageable pageable);

    // Soft-deleted
    Page<CommunicationLog> findByJobApplicationIdAndDeletedTrue(UUID jobAppId, Pageable pageable);

    // Recent entries
    List<CommunicationLog> findTop5ByJobApplicationUserIdAndDeletedFalseOrderByTimestampDesc(UUID userId);
}
