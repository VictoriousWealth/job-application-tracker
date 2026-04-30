package com.nick.job_application_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;
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
import com.nick.job_application_tracker.dto.create.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobSourceDetailDTO;
import com.nick.job_application_tracker.dto.response.JobSourceResponseDTO;
import com.nick.job_application_tracker.dto.update.JobSourceUpdateDTO;
import com.nick.job_application_tracker.service.implementation.JobSourceService;

@WebMvcTest(
    controllers = JobSourceController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class JobSourceControllerTest {

    private static final UUID JOB_SOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000801");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobSourceService service;

    @Test
    void getAllReturnsSources() throws Exception {
        JobSourceResponseDTO response = new JobSourceResponseDTO();
        response.setId(JOB_SOURCE_ID);
        response.setName("LinkedIn");

        when(service.getAllSources()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/job-sources"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(JOB_SOURCE_ID.toString()))
            .andExpect(jsonPath("$[0].name").value("LinkedIn"));
    }

    @Test
    void getByIdReturnsDetailedSource() throws Exception {
        JobSourceDetailDTO detail = new JobSourceDetailDTO();
        detail.setId(JOB_SOURCE_ID);
        detail.setName("Referral");

        when(service.getSourceById(JOB_SOURCE_ID)).thenReturn(Optional.of(detail));

        mockMvc.perform(get("/api/job-sources/{id}", JOB_SOURCE_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(JOB_SOURCE_ID.toString()))
            .andExpect(jsonPath("$.name").value("Referral"));
    }

    @Test
    void createReturnsCreatedSource() throws Exception {
        JobSourceCreateDTO request = new JobSourceCreateDTO();
        request.setName("Company Website");

        JobSourceResponseDTO response = new JobSourceResponseDTO();
        response.setId(JOB_SOURCE_ID);
        response.setName(request.getName());

        when(service.createSource(any(JobSourceCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/job-sources")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(JOB_SOURCE_ID.toString()))
            .andExpect(jsonPath("$.name").value("Company Website"));
    }

    @Test
    void updateReturnsUpdatedSource() throws Exception {
        JobSourceUpdateDTO request = new JobSourceUpdateDTO();
        request.setName("Recruiter");

        JobSourceDetailDTO response = new JobSourceDetailDTO();
        response.setId(JOB_SOURCE_ID);
        response.setName(request.getName());

        when(service.updateSource(any(UUID.class), any(JobSourceUpdateDTO.class))).thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/job-sources/{id}", JOB_SOURCE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Recruiter"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(service).deleteSource(JOB_SOURCE_ID);

        mockMvc.perform(delete("/api/job-sources/{id}", JOB_SOURCE_ID))
            .andExpect(status().isNoContent());
    }
}
