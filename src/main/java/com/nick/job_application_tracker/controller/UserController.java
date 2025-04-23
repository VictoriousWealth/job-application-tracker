package com.nick.job_application_tracker.controller;

import com.nick.job_application_tracker.dto.UserInfoDTO;
import com.nick.job_application_tracker.dto.UserUpdateDTO;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // User: Get own profile
    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser() {
        String email = getCurrentEmail();
        return ResponseEntity.ok(service.getUserInfoByEmail(email));
    }

    // User: Update own email/password
    @PatchMapping("/me")
    public ResponseEntity<UserInfoDTO> updateOwnProfile(@RequestBody @Valid UserUpdateDTO dto) {
        String email = getCurrentEmail();
        return ResponseEntity.ok(service.updateSelf(email, dto));
    }

    // User: Soft-delete (set enabled=false)
    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateSelf() {
        String email = getCurrentEmail();
        service.deactivateSelf(email);
        return ResponseEntity.noContent().build();
    }

    // Admin: Get all users
    @GetMapping
    public ResponseEntity<List<UserInfoDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    // Admin: Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserInfoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getUserInfoById(id));
    }

    // Admin: Update only enabled status
    @PatchMapping("/{id}")
    public ResponseEntity<UserInfoDTO> updateEnabled(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(service.updateEnabledStatus(id, dto.enabled));
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
} 
