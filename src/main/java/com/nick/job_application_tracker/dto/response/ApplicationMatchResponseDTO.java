package com.nick.job_application_tracker.dto.response;

import java.util.List;
import java.util.UUID;

public record ApplicationMatchResponseDTO(
    UUID applicationId,
    String company,
    String jobTitle,
    double overallScore,
    List<String> matchedSkills,
    List<String> missingSkills,
    List<String> keywordMatches,
    List<String> keywordGaps,
    DocumentMatchResponseDTO recommendedResume,
    DocumentMatchResponseDTO recommendedCoverLetter,
    List<String> recommendations
) {
}
