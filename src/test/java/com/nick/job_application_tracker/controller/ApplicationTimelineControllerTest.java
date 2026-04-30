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
import com.nick.job_application_tracker.dto.create.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.dto.detail.ApplicationTimelineDetailDTO;
import com.nick.job_application_tracker.dto.response.ApplicationTimelineResponseDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.service.implementation.ApplicationTimelineService;

@WebMvcTest(
    controllers = ApplicationTimelineController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class ApplicationTimelineControllerTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000000301");
    private static final UUID TIMELINE_ID = UUID.fromString("00000000-0000-0000-0000-000000000302");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApplicationTimelineService service;

    @Test
    void testGetForJob() throws Exception {
        ApplicationTimelineResponseDTO dto = new ApplicationTimelineResponseDTO();
        dto.setId(TIMELINE_ID);
        dto.setEventType(ApplicationTimeline.EventType.SUBMITTED);
        dto.setEventTime(LocalDateTime.of(2025, 6, 1, 12, 30));
        dto.setDescription("Phone call with recruiter");

        when(service.getByJobAppId(JOB_APP_ID)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/timelines/job/{jobAppId}", JOB_APP_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(TIMELINE_ID.toString()))
            .andExpect(jsonPath("$[0].eventType").value("SUBMITTED"))
            .andExpect(jsonPath("$[0].description").value("Phone call with recruiter"));
    }

    @Test
    void testCreate() throws Exception {
        ApplicationTimelineCreateDTO request = new ApplicationTimelineCreateDTO();
        request.setEventType(ApplicationTimeline.EventType.UPDATED);
        request.setEventTime(LocalDateTime.of(2025, 6, 2, 14, 0));
        request.setDescription("Technical interview");
        request.setJobApplicationId(JOB_APP_ID);

        ApplicationTimelineDetailDTO response = new ApplicationTimelineDetailDTO();
        response.setId(TIMELINE_ID);
        response.setEventType(request.getEventType());
        response.setEventTime(request.getEventTime());
        response.setDescription(request.getDescription());
        response.setJobApplicationId(JOB_APP_ID);

        when(service.create(any(ApplicationTimelineCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/timelines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(TIMELINE_ID.toString()))
            .andExpect(jsonPath("$.eventType").value("UPDATED"))
            .andExpect(jsonPath("$.jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(service).delete(TIMELINE_ID);

        mockMvc.perform(delete("/api/timelines/{id}", TIMELINE_ID))
            .andExpect(status().isNoContent());
    }

    @Test
    void testCreateBadRequestForMissingFields() throws Exception {
        mockMvc.perform(post("/api/timelines")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"description\":\"Missing required fields\"}"))
            .andExpect(status().isBadRequest());
    }
}
