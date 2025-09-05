package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.FollowUpReminder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface FollowUpReminderRepositoryCustom {

    Page<FollowUpReminder> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable);

    Page<FollowUpReminder> searchByNote(UUID userId, String keyword, Pageable pageable);
}
