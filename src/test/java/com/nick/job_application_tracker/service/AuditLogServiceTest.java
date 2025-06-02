package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.Role;
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
    void setUp() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("dummyPassword"); 
        user.setEnabled(true);             
        user.setRole(Role.BASIC);           

        user = userRepo.saveAndFlush(user);
        userId = user.getId();

        var auth = new UsernamePasswordAuthenticationToken("test@example.com", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    @DisplayName("Should save an audit log and return DTO")
    public void testSaveAuditLog() {
        AuditLogDTO dto = new AuditLogDTO(
            null,
            "CREATE",
            "Created a new job application",
            null,
            null
        );

        AuditLogDTO saved = auditLogService.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAction()).isEqualTo("CREATE");
        assertThat(saved.getDescription()).isEqualTo("Created a new job application");
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should return all audit logs")
    void testFindAll() {
        User user = userRepo.findById(userId).orElseThrow();

        AuditLog log = new AuditLog();
        log.setAction(AuditLog.Action.DELETE);
        log.setDescription("Deleted an application");
        log.setCreatedAt(LocalDateTime.now());
        log.setPerformedBy(user);
        auditLogRepo.save(log);

        List<AuditLogDTO> logs = auditLogService.findAll();

        assertThat(logs).isNotEmpty();
        assertThat(logs).anyMatch(l ->
            l.getDescription().equals("Deleted an application") &&
            l.getAction().equals("DELETE") &&
            l.getUserId().equals(userId)
        );
    }

    @Test
    @DisplayName("Should log CREATE using logCreate method")
    void testLogCreate() {
        auditLogService.logCreate("Used logCreate method");

        List<AuditLog> logs = auditLogRepo.findAll();
        assertThat(logs).isNotEmpty();

        AuditLog log = logs.get(logs.size() - 1); // Most recent

        assertThat(log.getAction()).isEqualTo(AuditLog.Action.CREATE);
        assertThat(log.getDescription()).isEqualTo("Used logCreate method");
        assertThat(log.getPerformedBy()).isNotNull();
        assertThat(log.getPerformedBy().getId()).isEqualTo(userId);
    }
}
