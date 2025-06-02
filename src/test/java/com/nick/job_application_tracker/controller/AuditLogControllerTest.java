package com.nick.job_application_tracker.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.service.AuditLogService;


@WebMvcTest(controllers = AuditLogController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService service;

    @Test
    @DisplayName("GET /api/audit-log - 200 OK with JSON list")
    void shouldReturnAllAuditLogs() throws Exception {
        AuditLogDTO log1 = new AuditLogDTO(1L, "CREATE", "Created job", LocalDateTime.now(), 1L);
        AuditLogDTO log2 = new AuditLogDTO(2L, "UPDATE", "Updated resume", LocalDateTime.now(), 1L);
        when(service.findAll()).thenReturn(List.of(log1, log2));

        mockMvc.perform(get("/api/audit-log"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].action").value("CREATE"))
            .andExpect(jsonPath("$[1].action").value("UPDATE"));
    }


    @Test
    @DisplayName("GET /api/audit-log - 200 OK with empty list")
    void shouldReturnEmptyListWhenNoAuditLogsExist() throws Exception {
        when(service.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/audit-log"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json("[]"));
    }


    @Test
    @DisplayName("GET /api/audit-log - 500 Internal Server Error when service throws")
    void shouldReturn500WhenServiceThrowsException() throws Exception {
        when(service.findAll()).thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(get("/api/audit-log"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("POST /api/audit-log - 201 Created with valid request")
    void shouldCreateAuditLogWithValidRequest() throws Exception {
        AuditLogDTO responseDTO = new AuditLogDTO(1L, "CREATE", "Created job", LocalDateTime.now(), 1L);

        when(service.save(any(AuditLogDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/audit-log")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "action": "CREATE",
                        "description": "Created job"
                    }
                """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.action").value("CREATE"))
            .andExpect(jsonPath("$.description").value("Created job"));
    }

    @Test
    @DisplayName("POST /api/audit-log - 400 Bad Request when required fields missing")
    void shouldReturn400WhenDescriptionIsMissing() throws Exception {
        mockMvc.perform(post("/api/audit-log")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "action": "CREATE"
                    }
                """))
            .andExpect(status().isBadRequest());
    }

}
