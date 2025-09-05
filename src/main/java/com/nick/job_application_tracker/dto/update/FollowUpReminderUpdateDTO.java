package com.nick.job_application_tracker.dto.update;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FollowUpReminderUpdateDTO {

    @NotNull
    private LocalDateTime remindOn;
    @NotBlank
    private String note;
    @NotNull
    private UUID jobApplicationId;


    // Getters and Setters

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

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

}
