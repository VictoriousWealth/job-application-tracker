package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.special.UserDetailDTO;
import com.nick.job_application_tracker.dto.special.UserUpdateDTO;
import com.nick.job_application_tracker.service.inter_face.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for user profile management and admin operations")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @Operation(summary = "Get current user's profile")
    @ApiResponse(responseCode = "200", description = "User info returned successfully",
        content = @Content(schema = @Schema(implementation = UserDetailDTO.class)))
    @GetMapping("/me")
    public ResponseEntity<UserDetailDTO> getCurrentUser() {
        String email = getCurrentEmail();
        return ResponseEntity.ok(service.getUserInfoByEmail(email));
    }

    @Operation(summary = "Update current user's email or password")
    @ApiResponse(responseCode = "200", description = "User info updated",
        content = @Content(schema = @Schema(implementation = UserDetailDTO.class)))
    @PatchMapping("/me")
    public ResponseEntity<UserDetailDTO> updateOwnProfile(@Valid @RequestBody UserUpdateDTO dto) {
        String email = getCurrentEmail();
        return ResponseEntity.ok(service.updateSelf(email, dto));
    }

    @Operation(summary = "Deactivate own account (soft delete)")
    @ApiResponse(responseCode = "204", description = "User deactivated")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deactivateSelf() {
        String email = getCurrentEmail();
        service.deactivateSelf(email);
        return ResponseEntity.noContent().build();
    }

    // ==============================
    // Admin endpoints below
    // ==============================

    @Operation(summary = "Admin: Get all users")
    @ApiResponse(responseCode = "200", description = "List of users returned",
        content = @Content(schema = @Schema(implementation = UserDetailDTO.class)))
    @GetMapping
    public ResponseEntity<List<UserDetailDTO>> getAllUsers() {
        return ResponseEntity.ok(service.getAllUsers());
    }

    @Operation(summary = "Admin: Get user by ID")
    @ApiResponse(responseCode = "200", description = "User info returned",
        content = @Content(schema = @Schema(implementation = UserDetailDTO.class)))
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getUserInfoById(id));
    }

    @Operation(summary = "Admin: Enable or disable a user")
    @ApiResponse(responseCode = "200", description = "User status updated",
        content = @Content(schema = @Schema(implementation = UserDetailDTO.class)))
    @PatchMapping("/{id}")
    public ResponseEntity<UserDetailDTO> updateEnabled(@PathVariable UUID id, @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(service.updateEnabledStatus(id, dto.enabled));
    }

    private String getCurrentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }
}
