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
import com.nick.job_application_tracker.dto.create.ResumeCreateDTO;
import com.nick.job_application_tracker.dto.response.ResumeResponseDTO;
import com.nick.job_application_tracker.service.implementation.ResumeService;

@WebMvcTest(
    controllers = ResumeController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class ResumeControllerTest {

    private static final UUID RESUME_ID = UUID.fromString("00000000-0000-0000-0000-000000001001");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResumeService service;

    @Test
    void getAllReturnsResumes() throws Exception {
        ResumeResponseDTO response = new ResumeResponseDTO();
        response.setId(RESUME_ID);
        response.setTitle("Backend Resume");
        response.setFilePath("/resumes/backend.pdf");

        when(service.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/resumes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(RESUME_ID.toString()))
            .andExpect(jsonPath("$[0].title").value("Backend Resume"));
    }

    @Test
    void uploadReturnsCreatedResume() throws Exception {
        ResumeCreateDTO request = new ResumeCreateDTO();
        request.setTitle("Platform Resume");
        request.setFilePath("/resumes/platform.pdf");

        ResumeResponseDTO response = new ResumeResponseDTO();
        response.setId(RESUME_ID);
        response.setTitle(request.getTitle());
        response.setFilePath(request.getFilePath());

        when(service.create(any(ResumeCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/resumes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.filePath").value("/resumes/platform.pdf"));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        doNothing().when(service).delete(RESUME_ID);

        mockMvc.perform(delete("/api/resumes/{id}", RESUME_ID))
            .andExpect(status().isNoContent());
    }
}
