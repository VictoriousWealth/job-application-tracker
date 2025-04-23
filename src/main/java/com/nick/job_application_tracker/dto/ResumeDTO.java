package com.nick.job_application_tracker.dto;

public class ResumeDTO {
    public Long id;
    public String filePath;

    public ResumeDTO(Long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }
}
