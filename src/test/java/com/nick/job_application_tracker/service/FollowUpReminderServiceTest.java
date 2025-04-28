package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.FollowUpReminderRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.UserRepository;

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
    private AuditLogService auditLogService; // ✅ ADD THIS

    private Long jobAppId;

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
    @DisplayName("Should save a new follow-up reminder and return DTO")
    void testSaveReminder() {
        FollowUpReminderCreateDTO dto = new FollowUpReminderCreateDTO();
        dto.setJobApplicationId(jobAppId);
        dto.setRemindOn(LocalDateTime.now().plusDays(5));
        dto.setNote("Follow up next week");

        FollowUpReminderDTO saved = service.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getJobApplicationId()).isEqualTo(jobAppId);
        assertThat(saved.getNote()).isEqualTo("Follow up next week");
    }

    @Test
    @DisplayName("Should throw exception for invalid job application ID")
    void testSaveReminderInvalidJobApp() {
        FollowUpReminderCreateDTO dto = new FollowUpReminderCreateDTO();
        dto.setJobApplicationId(999L);
        dto.setRemindOn(LocalDateTime.now());
        dto.setNote("Invalid test");

        assertThatThrownBy(() -> service.save(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Job Application not found"); // ✅ match your real service
    }


    @Test
    @DisplayName("Should return all reminders by job application ID")
    void testGetByJobAppId() {
        FollowUpReminder reminder1 = new FollowUpReminder();
        reminder1.setJobApplication(jobAppRepo.findById(jobAppId).get());
        reminder1.setNote("First");
        reminder1.setRemindOn(LocalDateTime.now().plusDays(1));

        FollowUpReminder reminder2 = new FollowUpReminder();
        reminder2.setJobApplication(jobAppRepo.findById(jobAppId).get());
        reminder2.setNote("Second");
        reminder2.setRemindOn(LocalDateTime.now().plusDays(2));

        reminderRepo.saveAll(List.of(reminder1, reminder2));

        List<FollowUpReminderDTO> results = service.getByJobAppId(jobAppId);

        assertThat(results).hasSize(2);
        assertThat(results).anyMatch(r -> r.getNote().equals("First"));
        assertThat(results).anyMatch(r -> r.getNote().equals("Second"));
    }

    @Test
    @DisplayName("Should delete a reminder by ID")
    void testDeleteReminder() {
        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setJobApplication(jobAppRepo.findById(jobAppId).get());
        reminder.setNote("To be deleted");
        reminder.setRemindOn(LocalDateTime.now().plusDays(3));
        reminder = reminderRepo.save(reminder);

        service.delete(reminder.getId());

        Optional<FollowUpReminder> deleted = reminderRepo.findById(reminder.getId());
        assertThat(deleted).isNotPresent();
    }
}
