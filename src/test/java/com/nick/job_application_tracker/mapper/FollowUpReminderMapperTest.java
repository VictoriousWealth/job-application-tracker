package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;

public class FollowUpReminderMapperTest {

    private final FollowUpReminderMapper mapper = new FollowUpReminderMapper();

    @Test
    void testToDTO() {
        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setRemindOn(LocalDateTime.of(2025, 4, 25, 14, 0));
        reminder.setNote("Check status");

        JobApplication job = new JobApplication();
        job.setId(42L);
        reminder.setJobApplication(job);

        FollowUpReminderDTO dto = mapper.toDTO(reminder);

        assertThat(dto.getRemindOn()).isEqualTo(LocalDateTime.of(2025, 4, 25, 14, 0));
        assertThat(dto.getNote()).isEqualTo("Check status");
        assertThat(dto.getJobApplicationId()).isEqualTo(42L);
    }

    @Test
    void testToEntity() {
        FollowUpReminderCreateDTO dto = new FollowUpReminderCreateDTO();
        dto.setRemindOn(LocalDateTime.of(2025, 5, 10, 9, 30));
        dto.setNote("Follow up with recruiter");
        dto.setJobApplicationId(100L);

        JobApplication jobApp = new JobApplication();
        jobApp.setId(100L);

        FollowUpReminder entity = mapper.toEntity(dto, jobApp);

        assertThat(entity.getRemindOn()).isEqualTo(dto.getRemindOn());
        assertThat(entity.getNote()).isEqualTo(dto.getNote());
        assertThat(entity.getJobApplication().getId()).isEqualTo(100L);
    }
}
