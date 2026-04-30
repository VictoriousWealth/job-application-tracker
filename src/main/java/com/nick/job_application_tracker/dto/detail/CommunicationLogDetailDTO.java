package com.nick.job_application_tracker.dto.detail;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nick.job_application_tracker.model.CommunicationLog.Direction;
import com.nick.job_application_tracker.model.CommunicationLog.Method;

public class CommunicationLogDetailDTO {
    
    private UUID id;
    private Method type;
    private Direction direction;
    private LocalDateTime timestamp;
    private String message;
    private UUID jobApplicationId;

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Method getType() {
        return type;
    }

    public void setType(Method type) {
        this.type = type;
    }

    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
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
