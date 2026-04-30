package com.nick.job_application_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.nick.job_application_tracker.dto.create.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.response.SkillTrackerResponseDTO;
import com.nick.job_application_tracker.service.implementation.SkillTrackerService;

@WebMvcTest(
    controllers = SkillTrackerController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class SkillTrackerControllerTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000001201");
    private static final UUID SKILL_ID = UUID.fromString("00000000-0000-0000-0000-000000001202");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SkillTrackerService service;

    @Test
    void getForJobReturnsTrackedSkills() throws Exception {
        SkillTrackerResponseDTO response = new SkillTrackerResponseDTO();
        response.setId(SKILL_ID);
        response.setSkillName("Distributed Systems");
        response.setJobApplicationId(JOB_APP_ID);

        when(service.getByJobAppId(JOB_APP_ID)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/skills/job/{jobAppId}", JOB_APP_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(SKILL_ID.toString()))
            .andExpect(jsonPath("$[0].skillName").value("Distributed Systems"));
    }

    @Test
    void createReturnsTrackedSkill() throws Exception {
        SkillTrackerCreateDTO request = new SkillTrackerCreateDTO();
        request.setSkillName("Java");
        request.setJobApplicationId(JOB_APP_ID);

        SkillTrackerResponseDTO response = new SkillTrackerResponseDTO();
        response.setId(SKILL_ID);
        response.setSkillName(request.getSkillName());
        response.setJobApplicationId(JOB_APP_ID);

        when(service.create(any(SkillTrackerCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/skills")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(SKILL_ID.toString()))
            .andExpect(jsonPath("$.jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(service).delete(SKILL_ID);

        mockMvc.perform(delete("/api/skills/{id}", SKILL_ID))
            .andExpect(status().isNoContent());
    }
}
