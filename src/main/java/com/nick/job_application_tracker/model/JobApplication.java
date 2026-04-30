package com.nick.job_application_tracker.model;

import java.time.LocalDateTime;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a job application submitted by a user.
 * Linked to optional documents and metadata like source and location.
 * Tracks the current status of the application and supports drafts.
 */
@Entity
@Table(name = "job_application")
public class JobApplication extends BaseEntity {

    public enum Status {
        DRAFT, APPLIED, INTERVIEW, OFFER, REJECTED;
        public static Status from(String value) {
            return Status.valueOf(value.toUpperCase());
        }

        public String getName() {
            return name();
        }
    }

    @NotNull
    @Column(nullable=false)
    private String jobTitle;

    @NotNull
    @Column(nullable=false)
    private String company;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "source_id", nullable=false)
    private JobSource source;

    @NotNull
    @Column(columnDefinition = "TEXT", nullable=false)
    private String jobDescription;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne
    @JoinColumn(name = "cover_letter_id")
    private CoverLetter coverLetter;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "applied_on")
    private LocalDateTime appliedOn;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    // --- Lifecycle Hooks ---

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = Status.DRAFT;
        }

        if (jobTitle != null) {
            jobTitle = jobTitle.trim();
        }

        if (company != null) {
            company = company.trim();
        }

        if (notes != null) {
            notes = notes.trim();
        }

        // Set appliedOn if status is APPLIED and no timestamp given
        if (appliedOn == null && status == Status.APPLIED) {
            appliedOn = LocalDateTime.now();
        }

    }

    @PreUpdate
    public void preUpdate() {
        if (appliedOn == null && status == Status.APPLIED) {
            appliedOn = LocalDateTime.now();
        }
    }

    // --- Constructors ---

    public JobApplication() {}

    public JobApplication(String jobTitle, String company, Status status, User user,
                          Location location, JobSource source, String jobDescription, Resume resume,
                          CoverLetter coverLetter, String notes, LocalDateTime appliedOn, LocalDateTime deadline) {
        this.jobTitle = jobTitle;
        this.company = company;
        this.status = status;
        this.user = user;
        this.location = location;
        this.source = source;
        this.jobDescription = jobDescription;
        this.resume = resume;
        this.coverLetter = coverLetter;
        this.notes = notes;
        this.appliedOn = appliedOn;
        this.deadline = deadline;
    }

    // --- Getters and Setters ---

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public JobSource getSource() {
        return source;
    }

    public void setSource(JobSource source) {
        this.source = source;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public CoverLetter getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(CoverLetter coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getAppliedOn() {
        return appliedOn;
    }

    public void setAppliedOn(LocalDateTime appliedOn) {
        this.appliedOn = appliedOn;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

}
