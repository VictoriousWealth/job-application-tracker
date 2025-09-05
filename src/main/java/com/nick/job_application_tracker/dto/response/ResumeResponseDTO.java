package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

public class ResumeResponseDTO {

    private String title;
    private String filePath;

    // --- Getters and Setters ---

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
