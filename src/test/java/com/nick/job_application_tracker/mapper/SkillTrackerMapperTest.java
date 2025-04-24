package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.SkillTrackerDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;

public class SkillTrackerMapperTest {

    @Test
    @DisplayName("Should map SkillTracker entity to DTO correctly")
    void testToDTO() {
        JobApplication jobApp = new JobApplication();
        jobApp.setId(100L);

        SkillTracker skill = new SkillTracker();
        skill.setId(1L);
        skill.setSkillName("Java");
        skill.setJobApplication(jobApp);

        SkillTrackerDTO dto = SkillTrackerMapper.toDTO(skill);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSkillName()).isEqualTo("Java");
        assertThat(dto.getJobApplicationId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("Should handle null JobApplication in SkillTracker entity")
    void testToDTONullJobApp() {
        SkillTracker skill = new SkillTracker();
        skill.setId(2L);
        skill.setSkillName("Python");
        skill.setJobApplication(null); // explicitly null

        SkillTrackerDTO dto = SkillTrackerMapper.toDTO(skill);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getSkillName()).isEqualTo("Python");
        assertThat(dto.getJobApplicationId()).isNull();
    }

    @Test
    @DisplayName("Should map SkillTrackerCreateDTO to SkillTracker entity")
    void testToEntity() {
        SkillTrackerCreateDTO dto = new SkillTrackerCreateDTO();
        dto.setSkillName("Spring Boot");

        JobApplication jobApp = new JobApplication();
        jobApp.setId(200L);

        SkillTracker entity = SkillTrackerMapper.toEntity(dto, jobApp);

        assertThat(entity).isNotNull();
        assertThat(entity.getSkillName()).isEqualTo("Spring Boot");
        assertThat(entity.getJobApplication()).isEqualTo(jobApp);
    }
}
