package com.nick.job_application_tracker.controller;

import java.time.LocalDateTime;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.service.ApplicationTimelineService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = ApplicationTimelineController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class ApplicationTimelineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationTimelineService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetForJob() throws Exception {
        Long jobAppId = 1L;
        ApplicationTimelineDTO dto = new ApplicationTimelineDTO();
        dto.setId(100L);
        dto.setEventType("PHONE_SCREEN");
        dto.setEventTime(LocalDateTime.of(2025, 6, 1, 12, 30));
        dto.setDescription("Phone call with recruiter");
        dto.setJobApplicationId(jobAppId);

        Mockito.when(service.getByJobAppId(jobAppId)).thenReturn(Collections.singletonList(dto));

        mockMvc.perform(get("/api/timelines/job/{jobAppId}", jobAppId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$[0].id").value(100L))
               .andExpect(jsonPath("$[0].eventType").value("PHONE_SCREEN"))
               .andExpect(jsonPath("$[0].description").value("Phone call with recruiter"))
               .andExpect(jsonPath("$[0].jobApplicationId").value(jobAppId));
    }

    @Test
    void testCreate() throws Exception {
        ApplicationTimelineDTO request = new ApplicationTimelineDTO();
        request.setEventType("INTERVIEW");
        request.setEventTime(LocalDateTime.of(2025, 6, 2, 14, 0));
        request.setDescription("Technical interview");
        request.setJobApplicationId(2L);

        ApplicationTimelineDTO response = new ApplicationTimelineDTO();
        response.setId(101L);
        response.setEventType(request.getEventType());
        response.setEventTime(request.getEventTime());
        response.setDescription(request.getDescription());
        response.setJobApplicationId(request.getJobApplicationId());

        Mockito.when(service.save(Mockito.any(ApplicationTimelineDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/timelines")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(101L))
               .andExpect(jsonPath("$.eventType").value("INTERVIEW"))
               .andExpect(jsonPath("$.description").value("Technical interview"))
               .andExpect(jsonPath("$.jobApplicationId").value(2L));
    }

    @Test
    void testDelete() throws Exception {
        Long idToDelete = 103L;
        Mockito.doNothing().when(service).delete(idToDelete);

        mockMvc.perform(delete("/api/timelines/{id}", idToDelete))
               .andExpect(status().isNoContent());
    }

    @Test
    void testGetForJob_NotFound() throws Exception {
        Long jobAppId = 99L;

        Mockito.when(service.getByJobAppId(jobAppId))
            .thenThrow(new EntityNotFoundException("Job Application not found"));

        mockMvc.perform(get("/api/timelines/job/{jobAppId}", jobAppId))
            .andExpect(status().isNotFound());
    }

    @Test
    void testCreate_BadRequest_MissingFields() throws Exception {
        // Missing eventType, eventTime, jobApplicationId
        String invalidJson = """
            {
                "description": "Missing required fields"
            }
        """;

        mockMvc.perform(post("/api/timelines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_BadRequest_InvalidJsonSyntax() throws Exception {
        String brokenJson = "{ \"eventType\": \"INTERVIEW\", "; // malformed JSON

        mockMvc.perform(post("/api/timelines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(brokenJson))
            .andExpect(status().isBadRequest());
    }


    @Test
    void testCreate_BadRequest_WrongTypes() throws Exception {
        // eventTime is expected to be a datetime, not a string
        String invalidJson = """
            {
                "eventType": "INTERVIEW",
                "eventTime": "not-a-date",
                "description": "Invalid date",
                "jobApplicationId": 2
            }
        """;

        mockMvc.perform(post("/api/timelines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testCreate_InternalServerError() throws Exception {
        ApplicationTimelineDTO dto = new ApplicationTimelineDTO();
        dto.setEventType("INTERVIEW");
        dto.setEventTime(LocalDateTime.now());
        dto.setDescription("Oops");
        dto.setJobApplicationId(1L);

        Mockito.when(service.save(Mockito.any(ApplicationTimelineDTO.class))).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/api/timelines")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isInternalServerError());
    }

    @Test
    void testDelete_NotFound() throws Exception {
        Long idToDelete = 404L;

        Mockito.doThrow(new EntityNotFoundException("Timeline not found"))
            .when(service).delete(idToDelete);

        mockMvc.perform(delete("/api/timelines/{id}", idToDelete))
            .andExpect(status().isNotFound());
    }


}
