package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.model.AuditLog.Action;

public class AuditLogMapperTest {

    private final AuditLogMapper mapper = new AuditLogMapper();

    @Test
    void testToDTO() {
        User user = new User();
        user.setId(42L);

        AuditLog log = new AuditLog();
        log.setId(1L);
        log.setAction(Action.CREATE);
        log.setDescription("Created a new application");
        log.setCreatedAt(LocalDateTime.of(2023, 10, 5, 12, 0));
        log.setUser(user);

        AuditLogDTO dto = mapper.toDTO(log);

        assertThat(dto).isNotNull();
        assertThat(dto.id).isEqualTo(1L);
        assertThat(dto.action).isEqualTo("CREATE");
        assertThat(dto.description).isEqualTo("Created a new application");
        assertThat(dto.createdAt).isEqualTo(LocalDateTime.of(2023, 10, 5, 12, 0));
        assertThat(dto.userId).isEqualTo(42L);
    }

    @Test
    void testToEntity() {
        AuditLogDTO dto = new AuditLogDTO();
        dto.id = 2L;
        dto.action = "UPDATE";
        dto.description = "Updated the resume";
        dto.createdAt = LocalDateTime.of(2024, 1, 1, 9, 30);
        dto.userId = 99L;

        AuditLog log = mapper.toEntity(dto);

        assertThat(log).isNotNull();
        assertThat(log.getAction()).isEqualTo(Action.UPDATE);
        assertThat(log.getDescription()).isEqualTo("Updated the resume");
        assertThat(log.getCreatedAt()).isEqualTo(LocalDateTime.of(2024, 1, 1, 9, 30));
        assertThat(log.getUser().getId()).isEqualTo(99L);
    }
}
