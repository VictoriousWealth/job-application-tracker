package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

import com.nick.job_application_tracker.model.Attachment.Type;

public class AttachmentResponseDTO {
    
    private UUID id;
    private Type type;
    private String filePath;

    // --- Getters and Setters ---

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
}
