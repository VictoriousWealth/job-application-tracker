package com.nick.job_application_tracker.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;


@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.properties") 
public class AttachmentRepositoryTest {

    @Autowired
    private AttachmentRepository attachmentRepo;

    @Autowired
    private JobApplicationRepository jobApplicationRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should return attachments by job application ID")
    public void testFindByJobApplicationId() {
        // Setup User
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password123");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        // Setup JobApplication
        JobApplication jobApp = new JobApplication();
        jobApp.setUser(user);
        jobApp.setCompany("TestCorp");
        jobApp.setJobTitle("QA Engineer");
        jobApp.setStatus(JobApplication.Status.APPLIED);
        jobApp = jobApplicationRepo.save(jobApp);

        // Create Attachments
        Attachment a1 = new Attachment();
        a1.setJobApplication(jobApp);
        a1.setFilePath("/files/desc1.pdf");
        a1.setType(Attachment.Type.JOB_DESCRIPTION);

        Attachment a2 = new Attachment();
        a2.setJobApplication(jobApp);
        a2.setFilePath("/files/offer1.pdf");
        a2.setType(Attachment.Type.OFFER_LETTER);

        attachmentRepo.saveAll(List.of(a1, a2));

        // When
        List<Attachment> results = attachmentRepo.findByJobApplicationId(jobApp.getId());

        // Then
        assertThat(results).hasSize(2);
        assertThat(results).anyMatch(a -> a.getType() == Attachment.Type.JOB_DESCRIPTION);
        assertThat(results).anyMatch(a -> a.getType() == Attachment.Type.OFFER_LETTER);
    }

    @Test
    @DisplayName("Should return empty list for non-existent job application ID")
    public void testFindByInvalidJobApplicationId() {
        List<Attachment> results = attachmentRepo.findByJobApplicationId(-1L);
        assertThat(results).isEmpty();
    }
}
