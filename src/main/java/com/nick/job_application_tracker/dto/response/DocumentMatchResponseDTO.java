package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

public record DocumentMatchResponseDTO(
    UUID id,
    String title,
    double score,
    String rationale
) {
}
