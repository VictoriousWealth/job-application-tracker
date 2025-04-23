package com.nick.job_application_tracker.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "follow_up_reminder")
public class FollowUpReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "remind_on", nullable = false)
    private LocalDateTime remindOn;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id")
    private JobApplication jobApplication;

    @Column(columnDefinition = "TEXT")
    private String note;

    // Getters and Setters
    public Long getId() {
        return id;
    }

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
