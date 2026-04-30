package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.dto.LegacyIdAdapter;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.repository.custom.FollowUpReminderRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FollowUpReminderRepository extends JpaRepository<FollowUpReminder, UUID>, FollowUpReminderRepositoryCustom {

    Page<FollowUpReminder> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default java.util.List<FollowUpReminder> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    default java.util.List<FollowUpReminder> findByJobApplicationId(Long jobApplicationId) {
        return findByJobApplicationId(LegacyIdAdapter.fromLong(jobApplicationId));
    }

    default java.util.Optional<FollowUpReminder> findById(Long id) {
        return findById(LegacyIdAdapter.fromLong(id));
    }

    default void deleteById(Long id) {
        deleteById(LegacyIdAdapter.fromLong(id));
    }

    Page<FollowUpReminder> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    Page<FollowUpReminder> findByJobApplicationUserIdAndIsDraftTrueAndDeletedFalse(UUID userId, Pageable pageable);

    Page<FollowUpReminder> findByJobApplicationUserIdAndDeletedTrue(UUID userId, Pageable pageable);

    long countByJobApplicationUserIdAndDeletedFalse(UUID userId);
}
