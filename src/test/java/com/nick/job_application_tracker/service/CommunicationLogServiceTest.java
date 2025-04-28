package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.CommunicationLogRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
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
    private AuditLogService auditLogService; // ✅ ADD THIS

    private Long jobAppId;

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
    @DisplayName("Should save and return communication log as DTO")
    void testSaveCommunicationLog() {
        CommunicationLogDTO dto = new CommunicationLogDTO(
            null,
            "EMAIL",
            "OUTBOUND",
            LocalDateTime.now(),
            "Sent initial application follow-up",
            jobAppId
        );

        CommunicationLogDTO saved = communicationLogService.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMessage()).isEqualTo("Sent initial application follow-up");
        assertThat(saved.getType()).isEqualTo("EMAIL");
        assertThat(saved.getDirection()).isEqualTo("OUTBOUND");
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

        List<CommunicationLogDTO> result = communicationLogService.getByJobAppId(jobAppId);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getType()).isEqualTo("CALL");
        assertThat(result.get(0).getDirection()).isEqualTo("INBOUND");
    }

    @Test
    @DisplayName("Should delete communication log by ID")
    void testDeleteCommunicationLog() {
        CommunicationLog log = new CommunicationLog();
        log.setType(CommunicationLog.Method.EMAIL);
        log.setDirection(CommunicationLog.Direction.OUTBOUND);
        log.setMessage("Text reminder");
        log.setTimestamp(LocalDateTime.now());

        JobApplication job = new JobApplication();
        job.setId(jobAppId);
        log.setJobApplication(job);

        log = communicationRepo.save(log);
        Long id = log.getId();

        communicationLogService.delete(id);

        assertThat(communicationRepo.findById(id)).isNotPresent();
    }
}
