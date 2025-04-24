package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

public class AuditLogDTO {
    public Long id;
    public String action;
    public String description;
    public LocalDateTime createdAt;
    public Long userId;

    public AuditLogDTO() {}

    public AuditLogDTO(Long id, String action, String description, LocalDateTime createdAt, Long userId) {
        this.id = id;
        this.action = action;
        this.description = description;
        this.createdAt = createdAt;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    
}
