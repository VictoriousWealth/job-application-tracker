package com.nick.job_application_tracker.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record WorkspaceExportResponseDTO(
    LocalDateTime exportedAt,
    String userEmail,
    AnalyticsResponseDTO analytics,
    RecommendationResponseDTO recommendations,
    List<WorkspaceApplicationExportResponseDTO> applications,
    List<CalendarEventResponseDTO> calendarEvents
) {
}
