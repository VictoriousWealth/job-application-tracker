package com.nick.job_application_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.CommunicationLog.Direction;
import com.nick.job_application_tracker.model.CommunicationLog.Method;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CommunicationLogRepositoryTest {

    @Autowired
    private CommunicationLogRepository communicationLogRepo;

    @Autowired
    private JobApplicationRepository jobAppRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should save and retrieve CommunicationLog by JobApplication ID")
    public void testFindByJobApplicationId() {
        // Given a user
        User user = new User();
        user.setEmail("comm@example.com");
        user.setPassword("testpass");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        // And a job application
        JobApplication jobApp = new JobApplication();
        jobApp.setJobTitle("Support Engineer");
        jobApp.setCompany("HelpDesk Inc.");
        jobApp.setStatus(JobApplication.Status.APPLIED);
        jobApp.setUser(user);
        jobApp = jobAppRepo.save(jobApp);

        // And a communication log
        CommunicationLog log = new CommunicationLog();
        log.setJobApplication(jobApp);
        log.setTimestamp(LocalDateTime.now());
        log.setDirection(Direction.OUTBOUND);
        log.setType(Method.EMAIL);
        log.setMessage("Sent follow-up email.");
        communicationLogRepo.save(log);

        // When
        List<CommunicationLog> logs = communicationLogRepo.findByJobApplicationId(jobApp.getId());

        // Then
        assertThat(logs).isNotEmpty();
        assertThat(logs.get(0).getType()).isEqualTo(Method.EMAIL);
        assertThat(logs.get(0).getDirection()).isEqualTo(Direction.OUTBOUND);
        assertThat(logs.get(0).getMessage()).contains("follow-up");
        assertThat(logs.get(0).getJobApplication().getId()).isEqualTo(jobApp.getId());
    }

    @Test
    @DisplayName("Should return empty list for non-existent JobApplication ID")
    public void testFindByInvalidJobApplicationId() {
        // When
        List<CommunicationLog> logs = communicationLogRepo.findByJobApplicationId(-999L);

        // Then
        assertThat(logs).isEmpty();
    }
}
