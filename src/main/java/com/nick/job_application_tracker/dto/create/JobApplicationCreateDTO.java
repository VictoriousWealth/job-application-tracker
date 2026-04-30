package com.nick.job_application_tracker.dto.create;

import java.time.LocalDateTime;
import java.util.UUID;

import com.nick.job_application_tracker.model.JobApplication.Status;

import jakarta.validation.constraints.NotNull;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.controller.JobApplicationController;

/**
 * {@link JobApplication}
 * {@link JobApplicationController}
 */
public class JobApplicationCreateDTO {
    @NotNull
    public String jobTitle;
    @NotNull
    public String company;
    public Status status;
    public UUID locationId;
    public String locationCity;
    public String locationCountry;
    @NotNull
    public UUID sourceId;
    @NotNull
    public String jobDescription;
    public UUID resumeId;
    public UUID coverLetterId;
    public String notes;
    public LocalDateTime appliedOn;
    public LocalDateTime deadline;

    // Getters and Setters

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

    public UUID getLocationId() {
        return locationId;
    }

    public void setLocationId(UUID locationId) {
        this.locationId = locationId;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }

    public String getLocationCountry() {
        return locationCountry;
    }

    public void setLocationCountry(String locationCountry) {
        this.locationCountry = locationCountry;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public String getJobDescription() {
        return jobDescription;
    }
    
    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public UUID getResumeId() {
        return resumeId;
    }

    public void setResumeId(UUID resumeId) {
        this.resumeId = resumeId;
    }

    public UUID getCoverLetterId() {
        return coverLetterId;
    }

    public void setCoverLetterId(UUID coverLetterId) {
        this.coverLetterId = coverLetterId;
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
