package com.nick.job_application_tracker.dto;

import java.time.LocalDateTime;

import com.nick.job_application_tracker.model.JobApplication.Status;

public class JobApplicationDetailDTO {
    public Long id;
    public String jobTitle;
    public String company;
    public Status status;
    public String locationCity;
    public String locationCountry;
    public Long sourceId;
    public Long resumeId;
    public Long coverLetterId;
    public String notes;
    public LocalDateTime appliedOn;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public Long getResumeId() {
        return resumeId;
    }

    public void setResumeId(Long resumeId) {
        this.resumeId = resumeId;
    }

    public Long getCoverLetterId() {
        return coverLetterId;
    }

    public void setCoverLetterId(Long coverLetterId) {
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
    
}