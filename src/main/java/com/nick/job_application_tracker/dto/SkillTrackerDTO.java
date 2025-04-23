package com.nick.job_application_tracker.dto;

public class SkillTrackerDTO {
    private Long id;
    private String skillName;
    private Long jobApplicationId;

    public SkillTrackerDTO() {}

    public SkillTrackerDTO(Long id, String skillName, Long jobApplicationId) {
        this.id = id;
        this.skillName = skillName;
        this.jobApplicationId = jobApplicationId;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
