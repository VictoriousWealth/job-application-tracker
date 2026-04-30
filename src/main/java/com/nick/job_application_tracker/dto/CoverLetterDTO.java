package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class CoverLetterDTO {
    private UUID id;
    private String title;
    private String filePath;
    private String content;

    public CoverLetterDTO() {}

    public CoverLetterDTO(UUID id, String title, String filePath, String content) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.content = content;
    }

    public CoverLetterDTO(Long id, String title, String filePath, String content) {
        this(LegacyIdAdapter.fromLong(id), title, filePath, content);
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
