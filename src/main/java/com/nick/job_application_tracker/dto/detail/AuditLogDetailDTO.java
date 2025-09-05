package com.nick.job_application_tracker.dto.detail;

import java.util.UUID;

import com.nick.job_application_tracker.model.AuditLog.Action;

public class AuditLogDetailDTO {
    
    private UUID id;
    private Action action;
    private UUID userId;

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
}