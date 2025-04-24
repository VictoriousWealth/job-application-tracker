package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.UserInfoDTO;
import com.nick.job_application_tracker.dto.UserUpdateDTO;
import com.nick.job_application_tracker.mapper.UserMapper;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper mapper;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder, UserMapper mapper) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public UserInfoDTO getUserInfoByEmail(String email) {
        User user = repo.findByEmail(email).orElseThrow();
        return mapper.toDTO(user);
    }

    public UserInfoDTO getUserInfoById(Long id) {
        User user = repo.findById(id).orElseThrow();
        return mapper.toDTO(user);
    }

    public List<UserInfoDTO> getAllUsers() {
        return repo.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserInfoDTO updateSelf(String email, UserUpdateDTO dto) {
        User user = repo.findByEmail(email).orElseThrow();

        if (dto.email != null && !dto.email.isBlank()) {
            user.setEmail(dto.email);
        }
        if (dto.password != null && !dto.password.isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.password));
        }

        return mapper.toDTO(repo.save(user));
    }

    public void deactivateSelf(String email) {
        User user = repo.findByEmail(email).orElseThrow();
        user.setEnabled(false);
        repo.save(user);
    }

    public UserInfoDTO updateEnabledStatus(Long id, Boolean enabled) {
        User user = repo.findById(id).orElseThrow();
        user.setEnabled(enabled);
        return mapper.toDTO(repo.save(user));
    }
}
