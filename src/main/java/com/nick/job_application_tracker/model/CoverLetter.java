package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a user's cover letter for job applications.
 * Supports draft-saving with partial data.
 */
@Entity
@Table(name = "cover_letter")
public class CoverLetter extends BaseEntity {

    @NotNull
    @Column(nullable=false)
    private String title;

    @NotNull
    @Column(name = "file_path")
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String content;

    // --- Constructors ---

    public CoverLetter() {}

    public CoverLetter(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    // --- Lifecycle Hooks ---

    @PrePersist
    public void prePersist() {
        title = title.trim();
        filePath = filePath.trim();
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
