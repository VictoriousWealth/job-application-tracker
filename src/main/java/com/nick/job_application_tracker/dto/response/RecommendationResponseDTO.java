package com.nick.job_application_tracker.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record RecommendationResponseDTO(
    LocalDateTime generatedAt,
    List<RecommendationItemResponseDTO> items
) {
}
