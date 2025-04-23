package com.nick.job_application_tracker.dto;

public class ResumeDTO {
    private Long id;
    private String filePath;

    public ResumeDTO() {}

    public ResumeDTO(Long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
