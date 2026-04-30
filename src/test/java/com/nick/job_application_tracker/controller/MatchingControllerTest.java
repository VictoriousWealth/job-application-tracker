package com.nick.job_application_tracker.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.response.ApplicationMatchResponseDTO;
import com.nick.job_application_tracker.dto.response.DocumentMatchResponseDTO;
import com.nick.job_application_tracker.service.implementation.ApplicationMatchingService;

@WebMvcTest(
    controllers = MatchingController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class MatchingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApplicationMatchingService applicationMatchingService;

    @Test
    void matchApplicationReturnsFitAnalysis() throws Exception {
        when(applicationMatchingService.matchApplication(com.nick.job_application_tracker.TestIds.uuid(1)))
            .thenReturn(new ApplicationMatchResponseDTO(
                com.nick.job_application_tracker.TestIds.uuid(1),
                "OpenAI",
                "Backend Engineer",
                72.5,
                List.of("Java", "Spring Boot"),
                List.of("Docker"),
                List.of("java", "spring"),
                List.of("docker"),
                new DocumentMatchResponseDTO(com.nick.job_application_tracker.TestIds.uuid(2), "Backend Resume", 75.0, "Matched keywords: java, spring"),
                null,
                List.of("Attach the best available resume match: Backend Resume.")
            ));

        mockMvc.perform(get("/api/matching/applications/{id}", com.nick.job_application_tracker.TestIds.uuid(1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.overallScore").value(72.5))
            .andExpect(jsonPath("$.recommendedResume.title").value("Backend Resume"))
            .andExpect(jsonPath("$.missingSkills[0]").value("Docker"));
    }
}
