package com.nick.job_application_tracker.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nick.job_application_tracker.model.ApplicationTimeline.EventType;

public class ApplicationTimelineResponseDTO {

    public UUID id;
    public EventType eventType;
    public LocalDateTime eventTime;
    public String description;

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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