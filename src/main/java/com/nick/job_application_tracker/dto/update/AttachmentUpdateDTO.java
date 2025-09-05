package com.nick.job_application_tracker.dto.update;

import java.util.UUID;

import com.nick.job_application_tracker.model.Attachment.Type;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AttachmentUpdateDTO {
    @NotNull
    public Type type;
    @NotBlank
    public String filePath;
    @NotNull
    private UUID jobApplicationId;


    // Getters and Setters

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
