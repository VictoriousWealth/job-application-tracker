package com.nick.job_application_tracker.mapper;

import com.nick.job_application_tracker.dto.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.SkillTrackerDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;

public class SkillTrackerMapper {

    public static SkillTrackerDTO toDTO(SkillTracker skill) {
        return new SkillTrackerDTO(
            skill.getId(),
            skill.getSkillName(),
            skill.getJobApplication() != null ? skill.getJobApplication().getId() : null
        );
    }

    public static SkillTracker toEntity(SkillTrackerCreateDTO dto, JobApplication jobApplication) {
        SkillTracker skill = new SkillTracker();
        skill.setSkillName(dto.getSkillName());
        skill.setJobApplication(jobApplication);
        return skill;
    }
}
