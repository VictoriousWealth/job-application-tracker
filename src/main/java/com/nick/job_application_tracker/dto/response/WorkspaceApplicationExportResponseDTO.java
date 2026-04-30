package com.nick.job_application_tracker.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkspaceApplicationExportResponseDTO(
    UUID applicationId,
    String company,
    String jobTitle,
    String status,
    String sourceName,
    String location,
    LocalDateTime createdAt,
    LocalDateTime appliedOn,
    LocalDateTime deadline,
    String resumeTitle,
    String coverLetterTitle,
    String notes,
    String jobDescription,
    List<String> skills,
    List<String> attachments,
    List<String> communications,
    List<String> timeline,
    List<CalendarEventResponseDTO> scheduledCommunications,
    List<CalendarEventResponseDTO> followUpReminders
) {
}
