package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

public class CommunicationLogDTO {
    public Long id;
    public String type;
    public String direction;
    public LocalDateTime timestamp;
    public String message;
    public Long jobApplicationId;

    public CommunicationLogDTO() {}

    public CommunicationLogDTO(Long id, String type, String direction, LocalDateTime timestamp, String message, Long jobApplicationId) {
        this.id = id;
        this.type = type;
        this.direction = direction;
        this.timestamp = timestamp;
        this.message = message;
        this.jobApplicationId = jobApplicationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }
}
