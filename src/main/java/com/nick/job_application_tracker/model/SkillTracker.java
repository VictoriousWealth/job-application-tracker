package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Tracks a skill relevant to a specific job application.
 * Supports draft saving by allowing incomplete data.
 */
@Entity
@Table(name = "skill_tracker")
public class SkillTracker extends BaseEntity {

    @NotBlank
    @Column(nullable=false)
    private String skillName;

    @ManyToOne(optional = false)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;

    // --- Constructors ---

    public SkillTracker() {}

    public SkillTracker(String skillName, JobApplication jobApplication) {
        this.skillName = skillName;
        this.jobApplication = jobApplication;
    }

    // --- Lifecycle Hook ---

    @PrePersist
    public void prePersist() {
        skillName = skillName.trim();

    }

    // --- Getters and Setters ---

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public JobApplication getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobApplication jobApplication) {
        this.jobApplication = jobApplication;
    }

}
