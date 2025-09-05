package com.nick.job_application_tracker.dto.detail;

import java.util.UUID;

import com.nick.job_application_tracker.model.Attachment.Type;

public class AttachmentDetailDTO {
    
    private UUID id;
    private Type type;
    private String filePath;
    private UUID jobApplicationId;

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
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
    
}