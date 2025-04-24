package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.AttachmentRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
public class AttachmentServiceTest {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private AttachmentRepository attachmentRepo;

    @Autowired
    private JobApplicationRepository jobRepo;

    @Autowired
    private UserRepository userRepo;

    private Long jobApplicationId;

    @BeforeEach
    void setup() {
        // Create a user and job application
        User user = new User();
        user.setEmail("attach@example.com");
        user.setPassword("pass");
        user.setEnabled(true);
        user = userRepo.save(user);

        JobApplication job = new JobApplication();
        job.setUser(user);
        job.setCompany("AttachCorp");
        job.setJobTitle("Tester");
        job.setStatus(JobApplication.Status.APPLIED);
        job = jobRepo.save(job);

        jobApplicationId = job.getId();
    }

    @Test
    @Transactional
    @DisplayName("Should save and return attachment DTO")
    public void testSaveAttachment() {
        AttachmentDTO dto = new AttachmentDTO(null, "JOB_DESCRIPTION", "/path/doc.pdf", jobApplicationId);

        AttachmentDTO saved = attachmentService.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo("JOB_DESCRIPTION");
        assertThat(saved.getFilePath()).isEqualTo("/path/doc.pdf");
        assertThat(saved.getJobApplicationId()).isEqualTo(jobApplicationId);
    }

    @Test
    @Transactional
    @DisplayName("Should fetch all attachments for a JobApplication")
    public void testGetByJobApplicationId() {
        Attachment a1 = new Attachment();
        a1.setType(Attachment.Type.JOB_DESCRIPTION);
        a1.setFilePath("/path/a1.pdf");

        Attachment a2 = new Attachment();
        a2.setType(Attachment.Type.OFFER_LETTER);
        a2.setFilePath("/path/a2.pdf");

        JobApplication job = new JobApplication();
        job.setId(jobApplicationId);
        a1.setJobApplication(job);
        a2.setJobApplication(job);

        attachmentRepo.saveAll(List.of(a1, a2));

        List<AttachmentDTO> results = attachmentService.getByJobAppId(jobApplicationId);

        assertThat(results).hasSize(2);
        assertThat(results).anyMatch(d -> d.getFilePath().equals("/path/a1.pdf"));
        assertThat(results).anyMatch(d -> d.getFilePath().equals("/path/a2.pdf"));
    }

    @Test
    @Transactional
    @DisplayName("Should delete attachment by ID")
    public void testDeleteAttachment() {
        Attachment a = new Attachment();
        a.setType(Attachment.Type.JOB_DESCRIPTION);
        a.setFilePath("/delete/this.pdf");

        JobApplication job = new JobApplication();
        job.setId(jobApplicationId);
        a.setJobApplication(job);

        a = attachmentRepo.save(a);

        attachmentService.delete(a.getId());

        Optional<Attachment> deleted = attachmentRepo.findById(a.getId());
        assertThat(deleted).isEmpty();
    }
}
