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
 * Represents an upcoming communication event (e.g., interview, call) for a job application.
 * Supports draft saves for partially filled entries.
 */
@Entity
@Table(name = "scheduled_communication")
public class ScheduledCommunication extends BaseEntity {

    public enum Type {
        INTERVIEW, ONLINE_ASSESSMENT, CALL, IN_PERSON_ASSESSMENT;
        public static Type from(String value) {
            return Type.valueOf(value.toUpperCase());
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Type type;

    @NotNull
    @Column
    private LocalDateTime scheduledFor;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    // --- Constructors ---

    public ScheduledCommunication() {}

    public ScheduledCommunication(Type type, LocalDateTime scheduledFor, String notes, JobApplication jobApplication) {
        this.type = type;
        this.scheduledFor = scheduledFor;
        this.notes = notes;
        this.jobApplication = jobApplication;
    }

    // --- Lifecycle Hook ---

    @PrePersist
    public void prePersist() {
        notes = notes.trim();

    }

    // --- Getters and Setters ---

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

}
