package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommunicationLogDTO {
    public UUID id;
    public String type;
    public String direction;
    public LocalDateTime timestamp;
    public String message;
    public UUID jobApplicationId;

    public CommunicationLogDTO() {}

    public CommunicationLogDTO(UUID id, String type, String direction, LocalDateTime timestamp, String message, UUID jobApplicationId) {
        this.id = id;
        this.type = type;
        this.direction = direction;
        this.timestamp = timestamp;
        this.message = message;
        this.jobApplicationId = jobApplicationId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }
}
