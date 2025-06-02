package com.nick.job_application_tracker.mapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.AuditLog.Action;
import com.nick.job_application_tracker.model.User;

class AuditLogMapperTest {

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
        log.setPerformedBy(user);

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
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.id = 2L;
        dto.action = "UPDATE";
        dto.description = "Updated the resume";
        dto.createdAt = LocalDateTime.of(2024, 1, 1, 9, 30); // should be ignored
        dto.userId = 99L; // should be ignored

        User mockUser = new User();
        mockUser.setId(123L);

        // Act
        AuditLog log = mapper.toEntity(dto, mockUser);

        // Assert
        assertThat(log).isNotNull();
        assertThat(log.getAction()).isEqualTo(Action.UPDATE);
        assertThat(log.getDescription()).isEqualTo("Updated the resume");
        assertThat(log.getPerformedBy()).isEqualTo(mockUser); 
        assertThat(log.getPerformedBy().getId()).isEqualTo(123L);

        // This is optional because createdAt is set to now
        assertThat(log.getCreatedAt()).isNotNull();
    }

    @Test
    void toEntityShouldThrow400OnInvalidAction() {
        // Arrange
        AuditLogDTO dto = new AuditLogDTO();
        dto.action = "INVALID";
        dto.description = "Some log";

        User user = new User();
        user.setId(1L);

        // Act + Assert
        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            mapper.toEntity(dto, user);
        });

        // Optionally check the message or status
        assertThat(ex.getStatusCode().value()).isEqualTo(400);
        assertThat(ex.getReason()).contains("Invalid action type");
    }

}
