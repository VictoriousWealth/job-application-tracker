package com.nick.job_application_tracker.dto;

public class CoverLetterDTO {
    private Long id;
    private String title;
    private String filePath;
    private String content;

    // Constructors
    public CoverLetterDTO() {}

    public CoverLetterDTO(Long id, String title, String filePath, String content) {
        this.id = id;
        this.title = title;
        this.filePath = filePath;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
