package com.nick.job_application_tracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;

public class AttachmentDTO {
    
    @Null
    public Long id;
    
    @NotNull
    public String type;
    
    @NotNull
    public String filePath;
    
    @NotNull
    public Long jobApplicationId;

    public AttachmentDTO() {}

    public AttachmentDTO(Long id, String type, String filePath, Long jobApplicationId) {
        this.id = id;
        this.type = type;
        this.filePath = filePath;
        this.jobApplicationId = jobApplicationId;
    }

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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }
}
