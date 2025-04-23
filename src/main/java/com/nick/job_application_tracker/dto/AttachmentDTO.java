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
}
