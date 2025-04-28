package com.nick.job_application_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.properties") 
public class FollowUpReminderRepositoryTest {

    @Autowired
    private FollowUpReminderRepository reminderRepo;

    @Autowired
    private JobApplicationRepository jobAppRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should save and retrieve FollowUpReminder by job application ID")
    public void testFindByJobApplicationId() {
        // Create and save a user
        User user = new User();
        user.setEmail("reminder@test.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        // Create and save a job application
        JobApplication jobApp = new JobApplication();
        jobApp.setUser(user);
        jobApp.setCompany("ReminderCo");
        jobApp.setJobTitle("QA Tester");
        jobApp.setStatus(Status.APPLIED);
        jobApp = jobAppRepo.save(jobApp);

        // Create and save a follow-up reminder
        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setJobApplication(jobApp);
        reminder.setRemindOn(LocalDateTime.now().plusDays(7));
        reminder.setNote("Follow up in one week.");
        reminder = reminderRepo.save(reminder);

        // Retrieve by job application ID
        List<FollowUpReminder> reminders = reminderRepo.findByJobApplicationId(jobApp.getId());

        // Assertions
        assertThat(reminders).hasSize(1);
        assertThat(reminders.get(0).getNote()).contains("Follow up");
        assertThat(reminders.get(0).getJobApplication().getId()).isEqualTo(jobApp.getId());
    }

    @Test
    @DisplayName("Should return empty list for non-existent job application ID")
    public void testFindByInvalidJobApplicationId() {
        List<FollowUpReminder> reminders = reminderRepo.findByJobApplicationId(-999L);
        assertThat(reminders).isEmpty();
    }

    @Test
    @DisplayName("Should delete a FollowUpReminder")
    public void testDeleteFollowUpReminder() {
        // Setup - create user
        User user = new User();
        user.setEmail("delete_test@example.com");
        user.setPassword("password");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        // Setup - create job application
        JobApplication job = new JobApplication();
        job.setJobTitle("Dev");
        job.setCompany("TestCorp");
        job.setStatus(JobApplication.Status.APPLIED);
        job.setUser(user);
        job = jobAppRepo.save(job);

        // Setup - create follow-up reminder
        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setNote("Temporary Reminder");
        reminder.setRemindOn(LocalDateTime.now().plusDays(1));
        reminder.setJobApplication(job); // ✅ Set the required field

        reminder = reminderRepo.save(reminder);
        Long id = reminder.getId();

        // Act
        reminderRepo.deleteById(id);
        Optional<FollowUpReminder> found = reminderRepo.findById(id);

        // Assert
        assertThat(found).isNotPresent();
    }

}
