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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

}
