package com.nick.job_application_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.special.UserDetailDTO;
import com.nick.job_application_tracker.dto.special.UserUpdateDTO;
import com.nick.job_application_tracker.service.implementation.UserService;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    private static final UUID USER_ID = UUID.fromString("00000000-0000-0000-0000-000000001301");
    private static final UUID ADMIN_TARGET_ID = UUID.fromString("00000000-0000-0000-0000-000000001302");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService service;

    @Test
    @WithMockUser(username = "user@example.com")
    void getCurrentUserReturnsAuthenticatedProfile() throws Exception {
        UserDetailDTO response = new UserDetailDTO(USER_ID, "user@example.com", true, Set.of("BASIC"));

        when(service.getUserInfoByEmail("user@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(USER_ID.toString()))
            .andExpect(jsonPath("$.email").value("user@example.com"))
            .andExpect(jsonPath("$.roles[0]").value("BASIC"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void updateOwnProfilePassesAuthenticatedEmail() throws Exception {
        UserUpdateDTO request = new UserUpdateDTO();
        request.setEmail("updated@example.com");
        request.setPassword("new-password");

        UserDetailDTO response = new UserDetailDTO(USER_ID, "updated@example.com", true, Set.of("BASIC"));

        when(service.updateSelf(eq("user@example.com"), any(UserUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(patch("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void deactivateOwnProfileReturnsNoContent() throws Exception {
        doNothing().when(service).deactivateSelf("user@example.com");

        mockMvc.perform(delete("/api/users/me"))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getAllUsersReturnsAdminView() throws Exception {
        UserDetailDTO response = new UserDetailDTO(ADMIN_TARGET_ID, "candidate@example.com", true, Set.of("BASIC"));

        when(service.getAllUsers()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(ADMIN_TARGET_ID.toString()))
            .andExpect(jsonPath("$[0].email").value("candidate@example.com"));
    }
}
