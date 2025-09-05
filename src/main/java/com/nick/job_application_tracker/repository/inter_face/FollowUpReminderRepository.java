package com.nick.job_application_tracker.repository.inter_face;

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

    Page<FollowUpReminder> findByJobApplicationUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    Page<FollowUpReminder> findByJobApplicationUserIdAndIsDraftTrueAndDeletedFalse(UUID userId, Pageable pageable);

    Page<FollowUpReminder> findByJobApplicationUserIdAndDeletedTrue(UUID userId, Pageable pageable);

    long countByJobApplicationUserIdAndDeletedFalse(UUID userId);
}
