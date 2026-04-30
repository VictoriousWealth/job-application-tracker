package com.nick.job_application_tracker.dto.response;

import java.util.List;

public record AnalyticsResponseDTO(
    long totalApplications,
    long appliedCount,
    long interviewStageCount,
    long offerCount,
    long rejectedCount,
    double appliedToInterviewRate,
    double interviewToOfferRate,
    double appliedToOfferRate,
    double activePipelineRate,
    long overdueReminders,
    long upcomingScheduledEvents,
    List<SourceAnalyticsResponseDTO> sourcePerformance
) {
}
