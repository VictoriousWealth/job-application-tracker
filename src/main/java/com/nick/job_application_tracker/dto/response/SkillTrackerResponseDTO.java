package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

public class SkillTrackerResponseDTO {

    private String skillName;
    private UUID jobApplicationId;


    // --- Getters and Setters ---

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
