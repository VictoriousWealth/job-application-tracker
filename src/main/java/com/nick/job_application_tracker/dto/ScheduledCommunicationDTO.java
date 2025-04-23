package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

public class ScheduledCommunicationDTO {

    private Long id;
    private String type;
    private LocalDateTime scheduledFor;
    private String notes;
    private Long jobApplicationId;

    // Constructors
    public ScheduledCommunicationDTO() {}

    public ScheduledCommunicationDTO(Long id, String type, LocalDateTime scheduledFor, String notes, Long jobApplicationId) {
        this.id = id;
        this.type = type;
        this.scheduledFor = scheduledFor;
        this.notes = notes;
        this.jobApplicationId = jobApplicationId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }
}
