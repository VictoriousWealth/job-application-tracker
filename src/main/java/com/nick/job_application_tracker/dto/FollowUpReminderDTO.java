package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

public class FollowUpReminderDTO {
    private Long id;
    private LocalDateTime remindOn;
    private String note;
    private Long jobApplicationId;

    // Getters and Setters

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getRemindOn() {
        return remindOn;
    }

    public void setRemindOn(LocalDateTime remindOn) {
        this.remindOn = remindOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
