package com.nick.job_application_tracker.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource("classpath:application-test.properties") 
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should find user by email")
    public void testFindByEmail() {
        // Given
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("securepass");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        userRepo.save(user);

        // When
        Optional<User> found = userRepo.findByEmail("user@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("Should return empty when user email not found")
    public void testFindByEmailNotFound() {
        Optional<User> result = userRepo.findByEmail("nonexistent@example.com");
        assertThat(result).isEmpty();
    }
}
