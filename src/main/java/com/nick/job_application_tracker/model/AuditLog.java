package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Records business-level actions performed in the system (e.g., CREATE, UPDATE).
 * Populated manually via application logic, not HTTP logging.
 */
@Entity
@Table(name = "audit_log")
public class AuditLog extends BaseEntity {

    public enum Action {
        CREATE,
        UPDATE,
        DELETE;

        public static Action from(String value) {
            return Action.valueOf(value.toUpperCase());
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Action action;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User performedBy;

    // --- Constructors ---

    public AuditLog() {}

    public AuditLog(Action action, String description, User performedBy) {
        this.action = action;
        this.description = description;
        this.performedBy = performedBy;
    }

    // --- Lifecycle Hook ---

    @PrePersist
    public void prePersist() {
        if (description != null) {
            description = description.trim();
        }

    }

    // --- Getters and Setters ---

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

}
