package com.nick.job_application_tracker.dto.create;

import jakarta.validation.constraints.NotBlank;

public class CoverLetterCreateDTO {
    
    @NotBlank
    private String title;
    @NotBlank
    private String filePath;
    private String content;

    // Getters and Setters

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
