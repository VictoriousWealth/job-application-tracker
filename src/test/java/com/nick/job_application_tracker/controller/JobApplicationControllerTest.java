package com.nick.job_application_tracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.create.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.response.JobApplicationResponseDTO;
import com.nick.job_application_tracker.dto.update.JobApplicationUpdateDTO;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.service.implementation.JobApplicationService;

@WebMvcTest(
    controllers = JobApplicationController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class JobApplicationControllerTest {

    private static final UUID JOB_APPLICATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000701");
    private static final UUID SOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000702");
    private static final UUID LOCATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000703");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JobApplicationService service;

    @Test
    void getAllReturnsPagedApplications() throws Exception {
        JobApplicationResponseDTO item = new JobApplicationResponseDTO();
        item.setId(JOB_APPLICATION_ID);
        item.setJobTitle("Backend Engineer");
        item.setCompany("OpenAI");
        item.setStatus(Status.INTERVIEW);
        item.setSourceName("Referral");

        when(service.getAll(any())).thenReturn(new PageImpl<>(List.of(item)));

        mockMvc.perform(get("/api/job-applications"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].id").value(JOB_APPLICATION_ID.toString()))
            .andExpect(jsonPath("$.content[0].jobTitle").value("Backend Engineer"))
            .andExpect(jsonPath("$.content[0].status").value("INTERVIEW"));
    }

    @Test
    void getByIdReturnsDetailedApplication() throws Exception {
        JobApplicationDetailDTO detail = new JobApplicationDetailDTO();
        detail.setId(JOB_APPLICATION_ID);
        detail.setJobTitle("Backend Engineer");
        detail.setCompany("OpenAI");
        detail.setStatus(Status.APPLIED);
        detail.setSourceId(SOURCE_ID);
        detail.setLocationId(LOCATION_ID);
        detail.setJobDescription("Distributed systems role");

        when(service.getDetailById(JOB_APPLICATION_ID)).thenReturn(detail);

        mockMvc.perform(get("/api/job-applications/{id}", JOB_APPLICATION_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(JOB_APPLICATION_ID.toString()))
            .andExpect(jsonPath("$.company").value("OpenAI"))
            .andExpect(jsonPath("$.sourceId").value(SOURCE_ID.toString()));
    }

    @Test
    void createReturnsCreatedApplication() throws Exception {
        JobApplicationCreateDTO request = new JobApplicationCreateDTO();
        request.setJobTitle("Platform Engineer");
        request.setCompany("OpenAI");
        request.setStatus(Status.APPLIED);
        request.setSourceId(SOURCE_ID);
        request.setJobDescription("Platform role");
        request.setAppliedOn(LocalDateTime.of(2026, 5, 1, 10, 0));

        JobApplicationResponseDTO response = new JobApplicationResponseDTO();
        response.setId(JOB_APPLICATION_ID);
        response.setJobTitle(request.getJobTitle());
        response.setCompany(request.getCompany());
        response.setStatus(request.getStatus());
        response.setSourceName("LinkedIn");

        when(service.create(any(JobApplicationCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/job-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(JOB_APPLICATION_ID.toString()))
            .andExpect(jsonPath("$.jobTitle").value("Platform Engineer"));
    }

    @Test
    void updateReturnsUpdatedApplication() throws Exception {
        JobApplicationUpdateDTO request = new JobApplicationUpdateDTO();
        request.setJobTitle("Senior Platform Engineer");
        request.setCompany("OpenAI");
        request.setStatus(Status.INTERVIEW);
        request.setSourceId(SOURCE_ID);
        request.setJobDescription("Updated description");

        JobApplicationDetailDTO response = new JobApplicationDetailDTO();
        response.setId(JOB_APPLICATION_ID);
        response.setJobTitle(request.getJobTitle());
        response.setCompany(request.getCompany());
        response.setStatus(request.getStatus());
        response.setSourceId(SOURCE_ID);
        response.setJobDescription(request.getJobDescription());

        when(service.updateById(any(UUID.class), any(JobApplicationUpdateDTO.class))).thenReturn(response);

        mockMvc.perform(put("/api/job-applications/{id}", JOB_APPLICATION_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.jobTitle").value("Senior Platform Engineer"))
            .andExpect(jsonPath("$.status").value("INTERVIEW"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doReturn("No Content").when(service).deleteById(JOB_APPLICATION_ID);

        mockMvc.perform(delete("/api/job-applications/{id}", JOB_APPLICATION_ID))
            .andExpect(status().isNoContent());
    }
}
