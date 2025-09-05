package com.nick.job_application_tracker.dto.create;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nick.job_application_tracker.model.ApplicationTimeline.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationTimelineCreateDTO {
    
    @NotBlank
    public EventType eventType;
    @NotNull
    public LocalDateTime eventTime;
    @NotBlank
    public String description;
    @NotNull
    public UUID jobApplicationId;

    // Getters and Setters

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
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

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    
}
