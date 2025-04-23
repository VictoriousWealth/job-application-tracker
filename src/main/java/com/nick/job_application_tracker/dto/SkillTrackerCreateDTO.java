package com.nick.job_application_tracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SkillTrackerCreateDTO {

    @NotBlank
    private String skillName;

    @NotNull
    private Long jobApplicationId;

    public SkillTrackerCreateDTO() {}

    public SkillTrackerCreateDTO(String skillName, Long jobApplicationId) {
        this.skillName = skillName;
        this.jobApplicationId = jobApplicationId;
    }

    // Getters and Setters

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Long getJobApplicationId() {
        return jobApplicationId;
    }

    public void setJobApplicationId(Long jobApplicationId) {
        this.jobApplicationId = jobApplicationId;
    }
    
}
