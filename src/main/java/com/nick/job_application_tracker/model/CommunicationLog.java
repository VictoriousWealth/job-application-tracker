package com.nick.job_application_tracker.model;

import java.time.LocalDateTime;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a record of communication related to a job application.
 * Supports draft-saving (optional or incomplete fields).
 */
@Entity
@Table(name = "communication_log")
public class CommunicationLog extends BaseEntity {

    public enum Method {
        EMAIL, CALL, LINKEDIN, IN_PERSON;

        public Method from(String value) {
            return Method.valueOf(value.toUpperCase());
        }
    }

    public enum Direction {
        INBOUND, OUTBOUND;

        public Direction from(String value) {
            return Direction.valueOf(value.toUpperCase());
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Method type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Direction direction;

    @Column(nullable=false)
    private LocalDateTime timestamp;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable=false)
    private String message;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    // --- Lifecycle Hooks ---

    @PrePersist
    public void prePersist() {
        // Default timestamp to now if not provided and message exists (implying it's more than just a draft)
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }

    }

    // --- Constructors ---

    public CommunicationLog() {}

    public CommunicationLog(Method type, Direction direction, LocalDateTime timestamp,
                            String message, JobApplication jobApplication) {
        this.type = type;
        this.direction = direction;
        this.timestamp = timestamp;
        this.message = message;
        this.jobApplication = jobApplication;
    }

    // --- Getters and Setters ---

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

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

}
