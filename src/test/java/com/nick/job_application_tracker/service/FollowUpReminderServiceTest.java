package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nick.job_application_tracker.dto.create.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.response.FollowUpReminderResponseDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.implementation.FollowUpReminderService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

@SpringBootTest
public class FollowUpReminderServiceTest {

    @Autowired
    private FollowUpReminderService service;

    @Autowired
    private JobApplicationRepository jobAppRepo;

    @Autowired
    private FollowUpReminderRepository reminderRepo;

    @Autowired
    private UserRepository userRepo;

    @MockBean
    private AuditLogService auditLogService;

    private UUID jobAppId;

    @BeforeEach
    void setup() {
        reminderRepo.deleteAll();
        jobAppRepo.deleteAll();
        userRepo.deleteAll();

        User user = new User();
        user.setEmail("reminder@test.com");
        user.setPassword("password");
        user.setEnabled(true);
        user = userRepo.save(user);

        JobApplication jobApp = new JobApplication();
        jobApp.setUser(user);
        jobApp.setCompany("ReminderCorp");
        jobApp.setJobTitle("Reminder Engineer");
        jobApp.setStatus(JobApplication.Status.APPLIED);
        jobApp = jobAppRepo.save(jobApp);

        jobAppId = jobApp.getId();
    }

    @Test
    @DisplayName("Should create a new follow-up reminder")
    void testCreateReminder() {
        FollowUpReminderCreateDTO dto = new FollowUpReminderCreateDTO();
        dto.setJobApplicationId(jobAppId);
        dto.setRemindOn(LocalDateTime.now().plusDays(5));
        dto.setNote("Follow up next week");

        FollowUpReminderResponseDTO saved = service.create(dto);

        assertThat(saved.getJobApplicationId()).isEqualTo(jobAppId);
        assertThat(saved.getNote()).isEqualTo("Follow up next week");
    }

    @Test
    @DisplayName("Should throw exception for invalid job application ID")
    void testCreateReminderInvalidJobApp() {
        FollowUpReminderCreateDTO dto = new FollowUpReminderCreateDTO();
        dto.setJobApplicationId(com.nick.job_application_tracker.TestIds.uuid(999));
        dto.setRemindOn(LocalDateTime.now());
        dto.setNote("Invalid test");

        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Job Application not found");
    }

    @Test
    @DisplayName("Should return all reminders by job application ID")
    void testGetByJobAppId() {
        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setJobApplication(jobAppRepo.findById(jobAppId).orElseThrow());
        reminder.setNote("First");
        reminder.setRemindOn(LocalDateTime.now().plusDays(1));

        reminderRepo.save(reminder);

        List<FollowUpReminderResponseDTO> results = service.getByJobAppId(jobAppId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getNote()).isEqualTo("First");
    }
}
