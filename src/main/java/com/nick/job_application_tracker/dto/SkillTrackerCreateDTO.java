package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class SkillTrackerCreateDTO {
    private String skillName;
    private UUID jobApplicationId;

    public SkillTrackerCreateDTO() {}

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
