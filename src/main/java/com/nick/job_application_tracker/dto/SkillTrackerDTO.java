package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class SkillTrackerDTO {
    private UUID id;
    private String skillName;
    private UUID jobApplicationId;

    public SkillTrackerDTO() {}

    public SkillTrackerDTO(UUID id, String skillName, UUID jobApplicationId) {
        this.id = id;
        this.skillName = skillName;
        this.jobApplicationId = jobApplicationId;
    }

    public SkillTrackerDTO(Long id, String skillName, Long jobApplicationId) {
        this(LegacyIdAdapter.fromLong(id), skillName, LegacyIdAdapter.fromLong(jobApplicationId));
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

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public UUID getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(UUID jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = LegacyIdAdapter.fromLong(jobApplicationId);
    }
}
