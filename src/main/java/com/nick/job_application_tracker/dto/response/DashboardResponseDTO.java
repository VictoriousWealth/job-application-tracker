package com.nick.job_application_tracker.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record DashboardResponseDTO(
    LocalDate since,
    LocalDate until,
    long totalApplications,
    long applicationsInPeriod,
    long activeApplications,
    long closedApplications,
    Map<String, Long> statusBreakdown,
    Map<String, Long> sourceBreakdown,
    Map<String, Long> locationBreakdown,
    Map<String, Long> appliedTrend,
    List<CalendarEventResponseDTO> upcomingEvents
) {
}
