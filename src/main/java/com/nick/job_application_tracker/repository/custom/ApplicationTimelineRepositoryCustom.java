package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.ApplicationTimeline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ApplicationTimelineRepositoryCustom {

    List<ApplicationTimeline> findRecentEventsByUser(UUID userId, LocalDateTime since);

    long countByJobApplication(UUID jobAppId);

    long countFinalizedEventsByUser(UUID userId);
}
