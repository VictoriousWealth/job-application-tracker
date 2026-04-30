package com.nick.job_application_tracker.dto.response;

public record SourceAnalyticsResponseDTO(
    String sourceName,
    long totalApplications,
    long interviewStageCount,
    long offerCount,
    double appliedToInterviewRate,
    double appliedToOfferRate
) {
}
