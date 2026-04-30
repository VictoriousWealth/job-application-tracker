package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.SkillTrackerDTO;
import com.nick.job_application_tracker.dto.detail.SkillTrackerDetailDTO;
import com.nick.job_application_tracker.dto.response.SkillTrackerResponseDTO;
import com.nick.job_application_tracker.dto.update.SkillTrackerUpdateDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;

@Component
public class SkillTrackerMapper {

    public static SkillTrackerResponseDTO toResponseDTO(SkillTracker skill) {
        SkillTrackerResponseDTO dto = new SkillTrackerResponseDTO();
        dto.setId(skill.getId());
        dto.setSkillName(skill.getSkillName());
        dto.setJobApplicationId(skill.getJobApplication() != null ? skill.getJobApplication().getId() : null);
        return dto;
    }

    public static SkillTrackerDetailDTO toDetailDTO(SkillTracker skill) {
        SkillTrackerDetailDTO dto = new SkillTrackerDetailDTO();
        dto.setId(skill.getId());
        dto.setSkillName(skill.getSkillName());
        dto.setJobApplicationId(skill.getJobApplication() != null ? skill.getJobApplication().getId() : null);
        return dto;
    }

    public static SkillTrackerDTO toDTO(SkillTracker skill) {
        return new SkillTrackerDTO(
            skill.getId(),
            skill.getSkillName(),
            skill.getJobApplication() != null ? skill.getJobApplication().getId() : null
        );
    }

    public static SkillTracker toEntity(com.nick.job_application_tracker.dto.SkillTrackerCreateDTO dto, JobApplication jobApplication) {
        SkillTracker skill = new SkillTracker();
        skill.setSkillName(dto.getSkillName());
        skill.setJobApplication(jobApplication);
        return skill;
    }

    public static SkillTracker toEntity(com.nick.job_application_tracker.dto.create.SkillTrackerCreateDTO dto, JobApplication jobApplication) {
        SkillTracker skill = new SkillTracker();
        skill.setSkillName(dto.getSkillName());
        skill.setJobApplication(jobApplication);
        return skill;
    }

    public static SkillTracker updateEntityWithDTOInfo(SkillTracker skill, SkillTrackerUpdateDTO dto, JobApplication jobApplication) {
        skill.setSkillName(dto.getSkillName());
        skill.setJobApplication(jobApplication);
        return skill;
    }
}
