package com.nick.job_application_tracker.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record CalendarEventResponseDTO(
    String type,
    UUID applicationId,
    String company,
    String jobTitle,
    LocalDateTime startsAt,
    LocalDateTime endsAt,
    String title,
    String description
) {
}
