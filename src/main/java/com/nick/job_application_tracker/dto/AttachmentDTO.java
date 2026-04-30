package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class AttachmentDTO {
    public UUID id;
    public String type;
    public String filePath;
    public UUID jobApplicationId;

    public AttachmentDTO() {}

    public AttachmentDTO(UUID id, String type, String filePath, UUID jobApplicationId) {
        this.id = id;
        this.type = type;
        this.filePath = filePath;
        this.jobApplicationId = jobApplicationId;
    }

    public AttachmentDTO(Long id, String type, String filePath, Long jobApplicationId) {
        this(LegacyIdAdapter.fromLong(id), type, filePath, LegacyIdAdapter.fromLong(jobApplicationId));
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = LegacyIdAdapter.fromLong(id);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = LegacyIdAdapter.fromLong(jobApplicationId);
    }
}
