package com.nick.job_application_tracker.controller;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.SecurityConfig;
import com.nick.job_application_tracker.config.provider.CustomJwtAuthenticationProvider;
import com.nick.job_application_tracker.config.service.JwtService;
import com.nick.job_application_tracker.dto.SignupRequest;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.UserRepository;


@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;
    
    @MockBean
    private CustomJwtAuthenticationProvider customJwtAuthenticationProvider;


    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Should return 200 OK when signup is successful")
    void signupSuccess() throws Exception {
        SignupRequest request = new SignupRequest();
        request.email = "newusertest@example.com";
        request.password = "password123";

        Mockito.when(userRepository.findByEmail(request.email)).thenReturn(Optional.empty());
        Mockito.when(passwordEncoder.encode(request.password)).thenReturn("encodedPassword");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L); 
            user.setRole((Role.BASIC));
            return user;
        });
        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(null);



        mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Signup successful."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request if email is already in use")
    void signupEmailAlreadyExists() throws Exception {
        SignupRequest request = new SignupRequest();
        request.email = "existing@example.com";
        request.password = "password123";

        User existingUser = new User();
        existingUser.setEmail(request.email);
        existingUser.setPassword("hashed");
        existingUser.setRole(Role.BASIC);
        existingUser.setEnabled(true);

        Mockito.when(userRepository.findByEmail(request.email)).thenReturn(Optional.of(existingUser));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use."));
    }
}
