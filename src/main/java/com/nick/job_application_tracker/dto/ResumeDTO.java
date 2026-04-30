package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class ResumeDTO {
    private UUID id;
    private String title;
    private String filePath;

    public ResumeDTO() {}

    public ResumeDTO(UUID id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }

    public ResumeDTO(Long id, String filePath) {
        this(LegacyIdAdapter.fromLong(id), filePath);
    }

    public ResumeDTO(UUID id, String title, String filePath) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = LegacyIdAdapter.fromLong(id);
    }

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
