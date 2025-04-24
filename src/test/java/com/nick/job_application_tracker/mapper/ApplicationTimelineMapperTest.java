package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;

public class ApplicationTimelineMapperTest {

    @Test
    @DisplayName("Should convert entity to DTO correctly")
    void testToDTO() {
        JobApplication job = new JobApplication();
        job.setId(42L);

        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setId(1L);
        entity.setEventType(ApplicationTimeline.EventType.SUBMITTED);
        entity.setEventTime(LocalDateTime.of(2024, 5, 10, 14, 30));
        entity.setDescription("Technical Interview");
        entity.setJobApplication(job);

        ApplicationTimelineDTO dto = ApplicationTimelineMapper.toDTO(entity);

        assertThat(dto.id).isEqualTo(1L);
        assertThat(dto.eventType).isEqualTo("SUBMITTED");
        assertThat(dto.eventTime).isEqualTo(LocalDateTime.of(2024, 5, 10, 14, 30));
        assertThat(dto.description).isEqualTo("Technical Interview");
        assertThat(dto.jobApplicationId).isEqualTo(42L);
    }

    @Test
    @DisplayName("Should convert DTO to entity correctly")
    void testToEntity() {
        ApplicationTimelineDTO dto = new ApplicationTimelineDTO(
            99L,
            "SUBMITTED",
            LocalDateTime.of(2024, 6, 1, 9, 0),
            "Received offer letter",
            101L
        );

        ApplicationTimeline entity = ApplicationTimelineMapper.toEntity(dto);

        assertThat(entity.getEventType()).isEqualTo(ApplicationTimeline.EventType.SUBMITTED);
        assertThat(entity.getEventTime()).isEqualTo(LocalDateTime.of(2024, 6, 1, 9, 0));
        assertThat(entity.getDescription()).isEqualTo("Received offer letter");
        assertThat(entity.getJobApplication().getId()).isEqualTo(101L);
    }
}
