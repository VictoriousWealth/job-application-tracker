package com.nick.job_application_tracker.dto;

public class AttachmentDTO {
    public Long id;
    public String type;
    public String filePath;
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
