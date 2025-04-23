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
}
