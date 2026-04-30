package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ApplicationTimelineCreateDTO {
    public UUID id;
    public String eventType;
    public LocalDateTime eventTime;
    public String description;
    public UUID jobApplicationId;

    public ApplicationTimelineCreateDTO() {}

    public ApplicationTimelineCreateDTO(UUID id, String eventType, LocalDateTime eventTime, String description, UUID jobApplicationId) {
        this.id = id;
        this.eventType = eventType;
        this.eventTime = eventTime;
        this.description = description;
        this.jobApplicationId = jobApplicationId;
    }

    public ApplicationTimelineCreateDTO(Long id, String eventType, LocalDateTime eventTime, String description, Long jobApplicationId) {
        this(LegacyIdAdapter.fromLong(id), eventType, eventTime, description, LegacyIdAdapter.fromLong(jobApplicationId));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = LegacyIdAdapter.fromLong(id);
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

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = LegacyIdAdapter.fromLong(jobApplicationId);
    }
}
