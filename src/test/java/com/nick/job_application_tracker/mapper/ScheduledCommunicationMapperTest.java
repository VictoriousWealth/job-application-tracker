package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.ScheduledCommunicationDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.ScheduledCommunication.Type;

public class ScheduledCommunicationMapperTest {

    @Test
    @DisplayName("Should map ScheduledCommunication entity to DTO correctly")
    void testToDTO() {
        JobApplication job = new JobApplication();
        job.setId(42L);

        ScheduledCommunication entity = new ScheduledCommunication();
        entity.setId(10L);
        entity.setType(Type.INTERVIEW);
        entity.setScheduledFor(LocalDateTime.of(2025, 5, 10, 15, 30));
        entity.setNotes("Follow up email");
        entity.setJobApplication(job);

        ScheduledCommunicationDTO dto = ScheduledCommunicationMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getType()).isEqualTo("INTERVIEW");
        assertThat(dto.getScheduledFor()).isEqualTo(LocalDateTime.of(2025, 5, 10, 15, 30));
        assertThat(dto.getNotes()).isEqualTo("Follow up email");
        assertThat(dto.getJobApplicationId()).isEqualTo(42L);
    }

    @Test
    @DisplayName("Should handle null job application in entity when mapping to DTO")
    void testToDTONullJobApp() {
        ScheduledCommunication entity = new ScheduledCommunication();
        entity.setId(11L);
        entity.setType(Type.CALL);
        entity.setScheduledFor(LocalDateTime.now());
        entity.setNotes("Call scheduled");
        entity.setJobApplication(null); // Explicitly null

        ScheduledCommunicationDTO dto = ScheduledCommunicationMapper.toDTO(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getJobApplicationId()).isNull();
    }

    @Test
    @DisplayName("Should map DTO and JobApplication to ScheduledCommunication entity correctly")
    void testToEntity() {
        ScheduledCommunicationCreateDTO dto = new ScheduledCommunicationCreateDTO();
        dto.setType("ONLINE_ASSESSMENT");
        dto.setScheduledFor(LocalDateTime.of(2025, 6, 1, 10, 0));
        dto.setNotes("Initial meeting");

        JobApplication jobApp = new JobApplication();
        jobApp.setId(99L);

        ScheduledCommunication entity = ScheduledCommunicationMapper.toEntity(dto, jobApp);

        assertThat(entity).isNotNull();
        assertThat(entity.getType()).isEqualTo(Type.ONLINE_ASSESSMENT);
        assertThat(entity.getScheduledFor()).isEqualTo(LocalDateTime.of(2025, 6, 1, 10, 0));
        assertThat(entity.getNotes()).isEqualTo("Initial meeting");
        assertThat(entity.getJobApplication()).isEqualTo(jobApp);
    }
}
