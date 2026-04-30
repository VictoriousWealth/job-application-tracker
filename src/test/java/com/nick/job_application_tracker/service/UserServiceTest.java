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

import com.nick.job_application_tracker.dto.special.UserDetailDTO;
import com.nick.job_application_tracker.dto.special.UserUpdateDTO;
import com.nick.job_application_tracker.mapper.UserMapper;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.service.implementation.UserService;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        auditLogService = mock(AuditLogService.class);
        UserMapper userMapper = new UserMapper(); 
        userService = new UserService(userRepository, passwordEncoder, userMapper, auditLogService);
    }

    @Test
    @DisplayName("Should return user info by email")
    void testGetUserInfoByEmail() {
        User user = new User();
        user.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        user.setEmail("user@example.com");
        user.setEnabled(true);
        user.setRole(Role.BASIC);

        when(userRepository.findByEmailAndDeletedFalse("user@example.com")).thenReturn(Optional.of(user));

        UserDetailDTO dto = userService.getUserInfoByEmail("user@example.com");

        assertThat(dto.getEmail()).isEqualTo("user@example.com");
        assertThat(dto.getRoles()).contains("BASIC");
    }

    @Test
    @DisplayName("Should return user info by ID")
    void testGetUserInfoById() {
        User user = new User();
        user.setId(com.nick.job_application_tracker.TestIds.uuid(2));
        user.setEmail("id@example.com");
        user.setEnabled(true);
        user.setRole(Role.BASIC);

        when(userRepository.findById(com.nick.job_application_tracker.TestIds.uuid(2))).thenReturn(Optional.of(user));

        UserDetailDTO dto = userService.getUserInfoById(com.nick.job_application_tracker.TestIds.uuid(2));

        assertThat(dto.getEmail()).isEqualTo("id@example.com");
        assertThat(dto.getRoles()).contains("BASIC");
    }

    @Test
    @DisplayName("Should return all users")
    void testGetAllUsers() {
        User user1 = new User();
        user1.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        user1.setEmail("one@example.com");
        user1.setEnabled(true);
        user1.setRole(Role.BASIC);

        User user2 = new User();
        user2.setId(com.nick.job_application_tracker.TestIds.uuid(2));
        user2.setEmail("two@example.com");
        user2.setEnabled(false);
        user2.setRole((Role.BASIC));

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDetailDTO> users = userService.getAllUsers();

        assertThat(users).hasSize(2);
        assertThat(users).anyMatch(u -> u.getEmail().equals("one@example.com"));
        assertThat(users).anyMatch(u -> u.getEmail().equals("two@example.com"));
    }

    @Test
    @DisplayName("Should update user email and password")
    void testUpdateSelf() {
        User user = new User();
        user.setId(com.nick.job_application_tracker.TestIds.uuid(3));
        user.setEmail("old@example.com");
        user.setPassword("oldpass");
        user.setEnabled(true);
        user.setRole((Role.BASIC));

        UserUpdateDTO updateDTO = new UserUpdateDTO("new@example.com", "newpass");

        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedpass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UserDetailDTO updated = userService.updateSelf("old@example.com", updateDTO);

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
        user.setId(com.nick.job_application_tracker.TestIds.uuid(5));
        user.setEmail("admin@example.com");
        user.setEnabled(false);
        user.setRole((Role.BASIC));

        when(userRepository.findById(com.nick.job_application_tracker.TestIds.uuid(5))).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDetailDTO updated = userService.updateEnabledStatus(com.nick.job_application_tracker.TestIds.uuid(5), true);

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
        when(userRepository.findById(com.nick.job_application_tracker.TestIds.uuid(999))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserInfoById(com.nick.job_application_tracker.TestIds.uuid(999)))
                .isInstanceOf(NoSuchElementException.class);
    }
}
