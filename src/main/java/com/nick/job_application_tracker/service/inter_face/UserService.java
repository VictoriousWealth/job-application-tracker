package com.nick.job_application_tracker.service.inter_face;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.special.UserDetailDTO;
import com.nick.job_application_tracker.dto.special.UserUpdateDTO;
import com.nick.job_application_tracker.mapper.UserMapper;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;
    private final AuditLogService auditLogService;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder, UserMapper mapper, AuditLogService auditLogService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public UserDetailDTO getUserInfoByEmail(String email) {
        User user = repo.findByEmailAndDeletedFalse(email).orElseThrow();
        return mapper.toDTO(user);
    }

    public UserDetailDTO getUserInfoById(UUID id) {
        User user = repo.findById(id).orElseThrow();
        return mapper.toDTO(user);
    }

    public List<UserDetailDTO> getAllUsers() {
        return repo.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDetailDTO updateSelf(String email, UserUpdateDTO dto) {
        User user = repo.findByEmailAndDeletedFalse(email).orElseThrow();

        boolean updated = false;

        if (dto.email != null && !dto.email.isBlank() && !dto.email.equals(user.getEmail())) {
            user.setEmail(dto.email);
            updated = true;
        }
        if (dto.password != null && !dto.password.isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password));
            updated = true;
        }

        if (updated) {
            User saved = repo.save(user);
            auditLogService.logUpdate("Updated own user account with id: " + saved.getId());
            return mapper.toDTO(saved);
        }

        return mapper.toDTO(user);
    }

    public void deactivateSelf(String email) {
        User user = repo.findByEmailAndDeletedFalse(email).orElseThrow();
        user.setEnabled(false);
        repo.save(user);

        auditLogService.logDelete("Deactivated own user account with id: " + user.getId());
    }

    public UserDetailDTO updateEnabledStatus(UUID id, Boolean enabled) {
        User user = repo.findById(id).orElseThrow();
        user.setEnabled(enabled);
        User saved = repo.save(user);

        auditLogService.logUpdate("Updated enabled status for user with id: " + saved.getId());

        return mapper.toDTO(saved);
    }
}
