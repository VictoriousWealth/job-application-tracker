package com.nick.job_application_tracker.controller;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.nick.job_application_tracker.config.service.JwtService;
import com.nick.job_application_tracker.dto.*;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.UserRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Content;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Signup, login, token management and current user info")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Operation(summary = "User signup")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Signup successful"),
        @ApiResponse(responseCode = "400", description = "Email already in use")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.email);
        if (existing.isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }

        User newUser = new User();
        newUser.setEmail(request.email);
        newUser.setPassword(passwordEncoder.encode(request.password));
        newUser.setRole(Role.BASIC);
        newUser.setEnabled(true);
        userRepository.save(newUser);

        return ResponseEntity.ok(Map.of("successful", true, "message", "Signup successful."));
    }

    @Operation(summary = "User login")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful", 
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }

        String role = user.getRoles().iterator().next().getName();
        String token = jwtService.generateToken(user.getEmail(), role);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @Operation(summary = "Get authenticated user info")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Current user info retrieved",
            content = @Content(schema = @Schema(implementation = UserInfoDTO.class))),
        @ApiResponse(responseCode = "401", description = "Authentication is missing"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication is missing.");
        }

        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOpt.get();
        UserInfoDTO userInfo = new UserInfoDTO(
            user.getId(),
            user.getEmail(),
            user.isEnabled(),
            user.getRoles().stream().map(Enum::name).collect(Collectors.toSet())
        );

        return ResponseEntity.ok(userInfo);
    }

    @Operation(summary = "Refresh JWT token")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed",
            content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Missing authentication"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<?> refreshToken() throws NoSuchAlgorithmException, InvalidKeyException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing authentication");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        String role = user.getRoles().iterator().next().name();
        String newToken = jwtService.generateToken(email, role);

        return ResponseEntity.ok(new JwtResponse(newToken));
    }
}
