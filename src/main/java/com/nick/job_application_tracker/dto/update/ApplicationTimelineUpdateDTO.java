package com.nick.job_application_tracker.dto.update;

import java.time.LocalDateTime;

import com.nick.job_application_tracker.model.ApplicationTimeline.EventType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationTimelineUpdateDTO {
    
    @NotNull
    public EventType eventType;
    @NotNull
    public LocalDateTime eventTime;
    @NotBlank
    public String description;

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

}
