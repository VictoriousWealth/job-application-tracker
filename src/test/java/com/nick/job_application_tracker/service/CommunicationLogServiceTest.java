package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.nick.job_application_tracker.dto.create.CommunicationLogCreateDTO;
import com.nick.job_application_tracker.dto.response.CommunicationLogResponseDTO;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.CommunicationLogRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.implementation.CommunicationLogService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class CommunicationLogServiceTest {

    @Autowired
    private CommunicationLogService communicationLogService;

    @Autowired
    private CommunicationLogRepository communicationRepo;

    @Autowired
    private JobApplicationRepository jobAppRepo;

    @Autowired
    private UserRepository userRepo;

    @MockBean
    private AuditLogService auditLogService;

    private UUID jobAppId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setEmail("comlog@example.com");
        user.setPassword("securepass");
        user.setEnabled(true);
        user = userRepo.save(user);

        JobApplication job = new JobApplication();
        job.setUser(user);
        job.setCompany("Communication Co.");
        job.setJobTitle("Support Analyst");
        job.setStatus(JobApplication.Status.APPLIED);
        job = jobAppRepo.save(job);

        jobAppId = job.getId();
    }

    @Test
    @DisplayName("Should create and return a communication log response")
    void testCreateCommunicationLog() {
        CommunicationLogCreateDTO dto = new CommunicationLogCreateDTO();
        dto.setType(CommunicationLog.Method.EMAIL);
        dto.setDirection(CommunicationLog.Direction.OUTBOUND);
        dto.setTimestamp(LocalDateTime.now());
        dto.setMessage("Sent initial application follow-up");
        dto.setJobApplicationId(jobAppId);

        CommunicationLogResponseDTO saved = communicationLogService.create(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(CommunicationLog.Method.EMAIL);
        assertThat(saved.getDirection()).isEqualTo(CommunicationLog.Direction.OUTBOUND);
        assertThat(saved.getJobApplicationId()).isEqualTo(jobAppId);
    }

    @Test
    @DisplayName("Should retrieve communication logs by job application ID")
    void testGetByJobAppId() {
        CommunicationLog log = new CommunicationLog();
        log.setType(CommunicationLog.Method.CALL);
        log.setDirection(CommunicationLog.Direction.INBOUND);
        log.setMessage("Phone interview");
        log.setTimestamp(LocalDateTime.now());

        JobApplication job = new JobApplication();
        job.setId(jobAppId);
        log.setJobApplication(job);

        communicationRepo.save(log);

        List<CommunicationLogResponseDTO> result = communicationLogService.getByJobAppId(jobAppId);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getType()).isEqualTo(CommunicationLog.Method.CALL);
    }
}
