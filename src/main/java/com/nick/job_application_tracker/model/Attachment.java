package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents an uploaded file related to a job application.
 * Supports draft-saving of incomplete attachments.
 */
@Entity
@Table(name = "attachment")
public class Attachment extends BaseEntity {

    public enum Type {
        JOB_DESCRIPTION,
        OFFER_LETTER,
        INTERVIEW_PREP,
        REJECTION_LETTER,
        OTHER;
        
        public static Type from(String value) {
            return Type.valueOf(value.toUpperCase());
        }
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column
    private Type type;

    @NotNull
    @Column(name = "file_path", nullable=false)
    private String filePath;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    // --- Constructors ---

    public Attachment() {}

    public Attachment(Type type, String filePath, JobApplication jobApplication) {
        this.type = type;
        this.filePath = filePath;
        this.jobApplication = jobApplication;
    }

    // --- Lifecycle Hook ---

    @PrePersist
    public void prePersist() {
        if (filePath != null) {
            filePath = filePath.trim();
        }

    }

    // --- Getters and Setters ---

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }
}
