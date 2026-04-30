package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

public record RecommendationItemResponseDTO(
    String type,
    String priority,
    UUID applicationId,
    String company,
    String jobTitle,
    String title,
    String description,
    String suggestedAction
) {
}
