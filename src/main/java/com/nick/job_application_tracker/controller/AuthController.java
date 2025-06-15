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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.config.service.JwtService;
import com.nick.job_application_tracker.dto.JwtResponse;
import com.nick.job_application_tracker.dto.LoginRequest;
import com.nick.job_application_tracker.dto.SignupRequest;
import com.nick.job_application_tracker.dto.UserInfoDTO;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }
    
    
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
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws Exception {
        Optional<User> userOpt = userRepository.findByEmail(request.email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials.");
        }
        
        String role = user.getRoles().iterator().next().getName(); // safely extract the single role
        String token = jwtService.generateToken(user.getEmail(), role);
        return ResponseEntity.ok(new JwtResponse(token));
    }

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

        // Assuming users only have one role (as your current setup implies)
        String role = user.getRoles().iterator().next().name(); // Convert Enum to string

        String newToken = jwtService.generateToken(email, role);

        return ResponseEntity.ok(new JwtResponse(newToken));
    }



}
