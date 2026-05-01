package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.FollowUpReminder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowUpReminderRepository extends JpaRepository<FollowUpReminder, UUID> {

    Page<FollowUpReminder> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default java.util.List<FollowUpReminder> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    Optional<FollowUpReminder> findByIdAndJobApplicationUserIdAndDeletedFalse(UUID id, UUID userId);

    Page<FollowUpReminder> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    Page<FollowUpReminder> findByJobApplicationUserIdAndIsDraftTrueAndDeletedFalse(UUID userId, Pageable pageable);

    Page<FollowUpReminder> findByJobApplicationUserIdAndDeletedTrue(UUID userId, Pageable pageable);

    long countByJobApplicationUserIdAndDeletedFalse(UUID userId);
}
