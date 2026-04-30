package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class FollowUpReminderDTO {
    private UUID id;
    private LocalDateTime remindOn;
    private String note;
    private UUID jobApplicationId;

    public FollowUpReminderDTO() {}

    public FollowUpReminderDTO(UUID id, LocalDateTime remindOn, String note, UUID jobApplicationId) {
        this.id = id;
        this.remindOn = remindOn;
        this.note = note;
        this.jobApplicationId = jobApplicationId;
    }

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

}
