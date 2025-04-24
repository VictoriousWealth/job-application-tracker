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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    
}
