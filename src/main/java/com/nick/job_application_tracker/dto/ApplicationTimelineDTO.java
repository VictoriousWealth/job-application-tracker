package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

public class ApplicationTimelineDTO {
    public Long id;
    public String eventType;
    public LocalDateTime eventTime;
    public String description;
    public Long jobApplicationId;

    public ApplicationTimelineDTO() {}

    public ApplicationTimelineDTO(Long id, String eventType, LocalDateTime eventTime, String description, Long jobApplicationId) {
        this.id = id;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.description = description;
        this.jobApplicationId = jobApplicationId;
    }
}
