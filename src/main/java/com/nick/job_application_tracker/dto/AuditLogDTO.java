package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AuditLogDTO {
    public Object id;
    public String action;
    public String description;
    public LocalDateTime createdAt;
    public Object userId;

    public AuditLogDTO() {}

    public AuditLogDTO(UUID id, String action, String description, LocalDateTime timestamp, UUID userId) {
        this.id = id;
        this.action = action;
        this.description = description;
        this.createdAt = timestamp;
        this.userId = userId;
    }

    public AuditLogDTO(Long id, String action, String description, LocalDateTime timestamp, Long userId) {
        this(LegacyIdAdapter.fromLong(id), action, description, timestamp, LegacyIdAdapter.fromLong(userId));
    }

    public UUID getId() {
        if (id instanceof UUID uuid) {
            return uuid;
        }
        if (id instanceof Long legacyId) {
            return LegacyIdAdapter.fromLong(legacyId);
        }
        return null;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = LegacyIdAdapter.fromLong(id);
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

    public LocalDateTime getTimestamp() {
        return createdAt;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.createdAt = timestamp;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getUserId() {
        if (userId instanceof UUID uuid) {
            return uuid;
        }
        if (userId instanceof Long legacyId) {
            return LegacyIdAdapter.fromLong(legacyId);
        }
        return null;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setUserId(Long userId) {
        this.userId = LegacyIdAdapter.fromLong(userId);
    }
}
