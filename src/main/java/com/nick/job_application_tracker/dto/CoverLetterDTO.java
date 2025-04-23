package com.nick.job_application_tracker.dto;

public class CoverLetterDTO {
    public Long id;
    public String filePath;

    public CoverLetterDTO(Long id, String filePath) {
        this.id = id;
        this.filePath = filePath;
    }
}
