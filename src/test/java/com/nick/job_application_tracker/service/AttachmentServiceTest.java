package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nick.job_application_tracker.dto.create.AttachmentCreateDTO;
import com.nick.job_application_tracker.dto.response.AttachmentResponseDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.AttachmentRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.implementation.AttachmentService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

import jakarta.transaction.Transactional;

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

    @MockBean
    private AuditLogService auditLogService;

    private UUID jobApplicationId;

    @BeforeEach
    void setup() {
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
    @DisplayName("Should create and return attachment response")
    public void testCreateAttachment() {
        AttachmentCreateDTO dto = new AttachmentCreateDTO();
        dto.setType(Attachment.Type.JOB_DESCRIPTION);
        dto.setFilePath("/path/doc.pdf");
        dto.setJobApplicationId(jobApplicationId);

        AttachmentResponseDTO saved = attachmentService.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(Attachment.Type.JOB_DESCRIPTION);
        assertThat(saved.getJobApplicationId()).isEqualTo(jobApplicationId);
    }

    @Test
    @Transactional
    @DisplayName("Should fetch all attachments for a job application")
    public void testGetByJobApplicationId() {
        Attachment attachment = new Attachment();
        attachment.setType(Attachment.Type.JOB_DESCRIPTION);
        attachment.setFilePath("/path/a1.pdf");

        JobApplication job = new JobApplication();
        job.setId(jobApplicationId);
        attachment.setJobApplication(job);

        attachmentRepo.save(attachment);

        List<AttachmentResponseDTO> results = attachmentService.getByJobAppId(jobApplicationId);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFilePath()).isEqualTo("/path/a1.pdf");
    }
}
