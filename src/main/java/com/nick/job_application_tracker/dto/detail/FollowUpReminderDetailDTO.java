package com.nick.job_application_tracker.dto.detail;

import java.time.LocalDateTime;
import java.util.UUID;

public class FollowUpReminderDetailDTO {

    private UUID id;
    private LocalDateTime remindOn;
    private UUID jobApplicationId;
    private String note;

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getRemindOn() {
        return remindOn;
    }

    public void setRemindOn(LocalDateTime remindOn) {
        this.remindOn = remindOn;
    }

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
