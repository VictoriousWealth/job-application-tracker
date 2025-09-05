package com.nick.job_application_tracker.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonCreator;
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
 * Represents an event in the job application's timeline (e.g., CREATED, UPDATED).
 * Supports draft-saving for incomplete event records.
 */
@Entity
@Table(name = "application_timeline")
public class ApplicationTimeline extends BaseEntity {

    public enum EventType {
        APPLICATION_CREATED,
        APPLICATION_UPDATED,
        APPLICATION_SUBMITTED,
        ;

        @JsonCreator
        public static EventType from(String value) {
            return EventType.valueOf(value.toUpperCase());
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private EventType eventType;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @NotNull
    @Column(name = "event_time")
    private LocalDateTime eventTime;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Constructors ---

    public ApplicationTimeline() {}

    public ApplicationTimeline(EventType eventType, JobApplication jobApplication, LocalDateTime eventTime, String description) {
        this.eventType = eventType;
        this.jobApplication = jobApplication;
        this.eventTime = eventTime;
        this.description = description;
    }

    // --- Lifecycle Hook ---

    @PrePersist
    public void prePersist() {
        if (description != null) {
            description = description.trim();
        }

        if (eventTime == null && eventType != null) {
            eventTime = LocalDateTime.now();
        }
    }

    // --- Getters and Setters ---

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        this.isDraft = draft;
    }
}
