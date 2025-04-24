package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.nick.job_application_tracker.dto.UserInfoDTO;
import com.nick.job_application_tracker.dto.UserUpdateDTO;
import com.nick.job_application_tracker.mapper.UserMapper;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.UserRepository;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        UserMapper userMapper = new UserMapper(); 
        userService = new UserService(userRepository, passwordEncoder, userMapper);
    }

    @Test
    @DisplayName("Should return user info by email")
    void testGetUserInfoByEmail() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setEnabled(true);
        user.setRole(Role.BASIC);

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        UserInfoDTO dto = userService.getUserInfoByEmail("user@example.com");

        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getRoles()).contains("BASIC");
    }

    @Test
    @DisplayName("Should return user info by ID")
    void testGetUserInfoById() {
        User user = new User();
        user.setId(2L);
        user.setEmail("id@example.com");
        user.setEnabled(true);
        user.setRole(Role.BASIC);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        UserInfoDTO dto = userService.getUserInfoById(2L);

        assertThat(dto.getEmail()).isEqualTo("id@example.com");
        assertThat(dto.getRoles()).contains("BASIC");
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setEmail("one@example.com");
        user1.setEnabled(true);
        user1.setRole(Role.BASIC);

        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("two@example.com");
        user2.setEnabled(false);
        user2.setRole((Role.BASIC));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserInfoDTO> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).anyMatch(u -> u.getEmail().equals("one@example.com"));
        assertThat(users).anyMatch(u -> u.getEmail().equals("two@example.com"));
    }

    @Test
    @DisplayName("Should update user email and password")
    void testUpdateSelf() {
        User user = new User();
        user.setId(3L);
        user.setEmail("old@example.com");
        user.setPassword("oldpass");
        user.setEnabled(true);
        user.setRole((Role.BASIC));

        UserUpdateDTO updateDTO = new UserUpdateDTO("new@example.com", "newpass");

        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserInfoDTO updated = userService.updateSelf("old@example.com", updateDTO);

        assertThat(updated.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    @DisplayName("Should deactivate a user")
    void testDeactivateSelf() {
        User user = new User();
        user.setEmail("deact@example.com");
        user.setEnabled(true);

        when(userRepository.findByEmail("deact@example.com")).thenReturn(Optional.of(user));

        userService.deactivateSelf("deact@example.com");

        assertThat(user.isEnabled()).isFalse();
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should update user enabled status")
    void testUpdateEnabledStatus() {
        User user = new User();
        user.setId(5L);
        user.setEmail("admin@example.com");
        user.setEnabled(false);
        user.setRole((Role.BASIC));

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserInfoDTO updated = userService.updateEnabledStatus(5L, true);

        assertThat(updated.isEnabled()).isTrue();
    }

    @Test
    @DisplayName("Should throw when email not found")
    void testGetUserByEmailThrows() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfoByEmail("missing@example.com"))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("Should throw when ID not found")
    void testGetUserByIdThrows() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfoById(999L))
                .isInstanceOf(NoSuchElementException.class);
    }
}
