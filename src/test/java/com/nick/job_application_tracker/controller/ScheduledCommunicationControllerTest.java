package com.nick.job_application_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.create.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.ScheduledCommunicationDetailDTO;
import com.nick.job_application_tracker.dto.response.ScheduledCommunicationResponseDTO;
import com.nick.job_application_tracker.service.implementation.ScheduledCommunicationService;

@WebMvcTest(
    controllers = ScheduledCommunicationController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class ScheduledCommunicationControllerTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000001101");
    private static final UUID COMMUNICATION_ID = UUID.fromString("00000000-0000-0000-0000-000000001102");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduledCommunicationService service;

    @Test
    void getAllReturnsScheduledEvents() throws Exception {
        ScheduledCommunicationResponseDTO response = new ScheduledCommunicationResponseDTO();
        response.setId(COMMUNICATION_ID);
        response.setType("INTERVIEW");
        response.setScheduledFor(LocalDateTime.of(2026, 5, 3, 14, 0));
        response.setNotes("Virtual panel interview");
        response.setJobApplicationId(JOB_APP_ID);

        when(service.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/scheduled-communications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(COMMUNICATION_ID.toString()))
            .andExpect(jsonPath("$[0].type").value("INTERVIEW"));
    }

    @Test
    void getByIdReturnsDetailedEvent() throws Exception {
        ScheduledCommunicationDetailDTO detail = new ScheduledCommunicationDetailDTO();
        detail.setId(COMMUNICATION_ID);
        detail.setType("CALL");
        detail.setScheduledFor(LocalDateTime.of(2026, 5, 4, 16, 0));
        detail.setNotes("Prep call");
        detail.setJobApplicationId(JOB_APP_ID);

        when(service.getById(COMMUNICATION_ID)).thenReturn(detail);

        mockMvc.perform(get("/api/scheduled-communications/{id}", COMMUNICATION_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.notes").value("Prep call"));
    }

    @Test
    void createReturnsCreatedEvent() throws Exception {
        ScheduledCommunicationCreateDTO request = new ScheduledCommunicationCreateDTO();
        request.setType("INTERVIEW");
        request.setScheduledFor(LocalDateTime.of(2026, 5, 5, 9, 30));
        request.setNotes("Hiring manager interview");
        request.setJobApplicationId(JOB_APP_ID);

        ScheduledCommunicationResponseDTO response = new ScheduledCommunicationResponseDTO();
        response.setId(COMMUNICATION_ID);
        response.setType(request.getType());
        response.setScheduledFor(request.getScheduledFor());
        response.setNotes(request.getNotes());
        response.setJobApplicationId(JOB_APP_ID);

        when(service.create(any(ScheduledCommunicationCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/scheduled-communications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(COMMUNICATION_ID.toString()))
            .andExpect(jsonPath("$.jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(service).delete(COMMUNICATION_ID);

        mockMvc.perform(delete("/api/scheduled-communications/{id}", COMMUNICATION_ID))
            .andExpect(status().isNoContent());
    }
}
