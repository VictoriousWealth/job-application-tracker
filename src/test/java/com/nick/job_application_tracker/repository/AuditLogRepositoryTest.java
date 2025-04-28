package com.nick.job_application_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.properties") 
public class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should save and retrieve audit log entry")
    public void testSaveAndFindAuditLog() {
        // Given a user
        User user = new User();
        user.setEmail("logtester@example.com");
        user.setPassword("testpassword");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        // Create audit log entry
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setCreatedAt(LocalDateTime.now());
        log.setAction(AuditLog.Action.CREATE);
        log.setDescription("Created new job application");

        auditLogRepo.save(log);

        // When
        List<AuditLog> allLogs = auditLogRepo.findAll();

        // Then
        assertThat(allLogs).isNotEmpty();
        assertThat(allLogs.get(0).getUser().getEmail()).isEqualTo("logtester@example.com");
        assertThat(allLogs.get(0).getAction()).isEqualTo(AuditLog.Action.CREATE);
        assertThat(allLogs.get(0).getDescription()).contains("job application");
    }
}
