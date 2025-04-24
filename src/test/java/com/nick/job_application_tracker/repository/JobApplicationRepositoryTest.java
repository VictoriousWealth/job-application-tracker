package com.nick.job_application_tracker.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;


@ActiveProfiles("test")
@Transactional
@SpringBootTest
public class JobApplicationRepositoryTest {

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should return job applications by user ID")
    public void testFindByUserId() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("securepassword");
        user.setEnabled(true);
        user = userRepository.save(user);

        JobApplication app = new JobApplication();
        app.setJobTitle("Software Engineer");
        app.setCompany("OpenAI");
        app.setStatus(Status.APPLIED);
        app.setUser(user);
        jobApplicationRepository.save(app);

        // When
        List<JobApplication> found = jobApplicationRepository.findByUserId(user.getId());

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getCompany()).isEqualTo("OpenAI");
    }

    @Test
    @DisplayName("Should return job applications by status")
    public void testFindByStatus() {
        // Given
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("secure123");
        user.setEnabled(true);
        user = userRepository.save(user);

        JobApplication app = new JobApplication();
        app.setJobTitle("Backend Developer");
        app.setCompany("TechCorp");
        app.setStatus(Status.INTERVIEW);
        app.setUser(user);
        jobApplicationRepository.save(app);

        // When
        List<JobApplication> found = jobApplicationRepository.findByStatus(Status.INTERVIEW);

        // Then
        assertThat(found).isNotEmpty();
        assertThat(found).anyMatch(a -> "TechCorp".equals(a.getCompany()));
    }

    @Test
    @DisplayName("Should return empty list for non-existing user ID")
    public void testFindByInvalidUserId() {
        List<JobApplication> found = jobApplicationRepository.findByUserId(99999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list for unused status")
    public void testFindByUnusedStatus() {
        List<JobApplication> found = jobApplicationRepository.findByStatus(JobApplication.Status.REJECTED);
        assertThat(found).isEmpty();
    }

}
