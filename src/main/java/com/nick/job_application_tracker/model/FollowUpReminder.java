package com.nick.job_application_tracker.model;

import java.time.LocalDateTime;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a reminder to follow up on a job application.
 * Supports saving incomplete (draft) reminders.
 */
@Entity
@Table(name = "follow_up_reminder")
public class FollowUpReminder extends BaseEntity {

    @NotNull
    @Column(name = "remind_on", nullable=false)
    private LocalDateTime remindOn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    @NotBlank
    @Column(columnDefinition = "TEXT", nullable=false)
    private String note;

    // --- Constructors ---

    public FollowUpReminder() {}

    public FollowUpReminder(LocalDateTime remindOn, JobApplication jobApplication, String note) {
        this.remindOn = remindOn;
        this.jobApplication = jobApplication;
        this.note = note;
    }

    // --- Lifecycle Hooks ---

    @PrePersist
    public void prePersist() {
        note = note.trim();

    }

    // --- Getters and Setters ---

    public LocalDateTime getRemindOn() {
        return remindOn;
    }

    public void setRemindOn(LocalDateTime remindOn) {
        this.remindOn = remindOn;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
