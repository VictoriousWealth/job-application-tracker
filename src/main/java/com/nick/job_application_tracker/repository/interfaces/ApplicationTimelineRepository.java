package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.ApplicationTimeline;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing and managing {@link ApplicationTimeline} entities.
 * Supports pagination, filtering, soft deletes, and draft-aware querying.
 */
public interface ApplicationTimelineRepository
        extends JpaRepository<ApplicationTimeline, UUID> {

    // --- Ownership + soft delete filtering ---

    Page<ApplicationTimeline> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default List<ApplicationTimeline> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    Optional<ApplicationTimeline> findByIdAndJobApplicationUserIdAndDeletedFalse(UUID id, UUID userId);

    Page<ApplicationTimeline> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    // --- Draft state filters ---

    Page<ApplicationTimeline> findByJobApplicationIdAndIsDraftFalseAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    Page<ApplicationTimeline> findByJobApplicationIdAndIsDraftTrueAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    // --- EventType filtering ---

    Page<ApplicationTimeline> findByJobApplicationIdAndEventTypeAndDeletedFalse(UUID jobApplicationId, ApplicationTimeline.EventType eventType, Pageable pageable);

    // --- Time-based filtering ---

    Page<ApplicationTimeline> findByJobApplicationIdAndEventTimeBetweenAndDeletedFalse(UUID jobApplicationId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<ApplicationTimeline> findTop5ByJobApplicationUserIdAndDeletedFalseOrderByEventTimeDesc(UUID userId);

    Page<ApplicationTimeline> findByJobApplicationUserIdAndDescriptionContainingIgnoreCaseAndDeletedFalse(UUID userId, String keyword, Pageable pageable);


    // --- Soft-deleted view (optional for admin/recycle bin use) ---

    Page<ApplicationTimeline> findByJobApplicationIdAndDeletedTrue(UUID jobApplicationId, Pageable pageable);
}
