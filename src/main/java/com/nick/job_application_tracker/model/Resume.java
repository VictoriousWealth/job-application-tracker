package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a resume (CV) uploaded by the user.
 * Supports draft saving if the file path is not yet set.
 */
@Entity
@Table(name = "resume")
public class Resume extends BaseEntity {

    @NotBlank
    @Column(nullable=false)
    private String title;

    @NotBlank
    @Column(name = "file_path", nullable=false)
    private String filePath;

    // --- Constructors ---

    public Resume() {}

    public Resume(String title, String filePath) {
        this.title = title;
        this.filePath = filePath;
    }

    // --- Lifecycle Hook ---

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

}
