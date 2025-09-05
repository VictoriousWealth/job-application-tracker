package com.nick.job_application_tracker.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class FollowUpReminderResponseDTO {

    private LocalDateTime remindOn;
    private UUID jobApplicationId;
    private String note;

    // --- Getters and Setters ---

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
