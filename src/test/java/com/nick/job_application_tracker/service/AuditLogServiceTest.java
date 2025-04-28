package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.AuditLogRepository;
import com.nick.job_application_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@Transactional
@SpringBootTest
public class AuditLogServiceTest {

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private AuditLogRepository auditLogRepo;

    @Autowired
    private UserRepository userRepo;

    private Long userId;

    @BeforeEach
    void setup() {
        User user = new User();
        user.setEmail("audit@example.com");
        user.setPassword("auditpass");
        user.setEnabled(true);
        user = userRepo.save(user);

        userId = user.getId();
    }

    @Test
    @DisplayName("Should save an audit log and return DTO")
    public void testSaveAuditLog() {
        AuditLogDTO dto = new AuditLogDTO(
            null,
            "CREATE",
            "Created a new job application",
            LocalDateTime.now(),
            userId
        );

        AuditLogDTO saved = auditLogService.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAction()).isEqualTo("CREATE");
        assertThat(saved.getDescription()).isEqualTo("Created a new job application");
        assertThat(saved.getUserId()).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should return all audit logs")
    public void testFindAll() {
        AuditLog log = new AuditLog();
        log.setAction(AuditLog.Action.DELETE);
        log.setDescription("Deleted an application");
        log.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(userId);
        log.setUser(user);

        auditLogRepo.save(log);

        List<AuditLogDTO> logs = auditLogService.findAll();

        assertThat(logs).isNotEmpty();
        assertThat(logs).anyMatch(l -> l.getDescription().equals("Deleted an application"));
    }
}
