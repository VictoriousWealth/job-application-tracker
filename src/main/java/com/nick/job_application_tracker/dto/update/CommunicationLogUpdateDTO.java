package com.nick.job_application_tracker.dto.update;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nick.job_application_tracker.model.CommunicationLog.Direction;
import com.nick.job_application_tracker.model.CommunicationLog.Method;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CommunicationLogUpdateDTO {
    @NotBlank
    public Method type;
    @NotBlank
    public Direction direction;
    @NotNull
    public LocalDateTime timestamp;
    @NotBlank
    public String message;
    @NotNull
    public UUID jobApplicationId;

    // Getters and Setters

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
