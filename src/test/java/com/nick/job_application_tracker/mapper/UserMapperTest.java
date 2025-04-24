package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.UserInfoDTO;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

public class UserMapperTest {

    @Test
    @DisplayName("Should map User entity to UserInfoDTO correctly")
    void testToDTO() {
        User user = new User();
        user.setId(42L);
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setRole(Role.BASIC);

        UserMapper mapper = new UserMapper();
        UserInfoDTO dto = mapper.toDTO(user);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getEmail()).isEqualTo("test@example.com");
        assertThat(dto.isEnabled()).isTrue();
        assertThat(dto.getRoles()).containsExactlyInAnyOrder("BASIC");
    }
}
