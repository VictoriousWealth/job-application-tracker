package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ScheduledCommunicationDTO {
    private UUID id;
    private String type;
    private LocalDateTime scheduledFor;
    private String notes;
    private UUID jobApplicationId;

    public ScheduledCommunicationDTO() {}

    public ScheduledCommunicationDTO(UUID id, String type, LocalDateTime scheduledFor, String notes, UUID jobApplicationId) {
        this.id = id;
        this.type = type;
        this.scheduledFor = scheduledFor;
        this.notes = notes;
        this.jobApplicationId = jobApplicationId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

}
