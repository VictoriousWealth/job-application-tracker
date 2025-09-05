package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

import com.nick.job_application_tracker.model.AuditLog.Action;

public class AuditLogResponseDTO {
    
    private UUID id;
    private Action action;

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

}