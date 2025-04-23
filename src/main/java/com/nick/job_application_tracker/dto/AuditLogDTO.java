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
}
