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
import com.nick.job_application_tracker.dto.create.CoverLetterCreateDTO;
import com.nick.job_application_tracker.dto.response.CoverLetterResponseDTO;
import com.nick.job_application_tracker.service.implementation.CoverLetterService;

@WebMvcTest(
    controllers = CoverLetterController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class CoverLetterControllerTest {

    private static final UUID COVER_LETTER_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CoverLetterService service;

    @Test
    void getAllReturnsCurrentUsersCoverLetters() throws Exception {
        CoverLetterResponseDTO response = new CoverLetterResponseDTO();
        response.setId(COVER_LETTER_ID);
        response.setTitle("Backend Cover Letter");
        response.setFilePath("/letters/backend.pdf");
        response.setContent("Tailored cover letter content");

        when(service.findAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/cover-letters"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(COVER_LETTER_ID.toString()))
            .andExpect(jsonPath("$[0].title").value("Backend Cover Letter"))
            .andExpect(jsonPath("$[0].filePath").value("/letters/backend.pdf"));
    }

    @Test
    void createReturnsCreatedCoverLetter() throws Exception {
        CoverLetterCreateDTO request = new CoverLetterCreateDTO();
        request.setTitle("Product Engineer Letter");
        request.setFilePath("/letters/product.pdf");
        request.setContent("Concise tailored content");

        CoverLetterResponseDTO response = new CoverLetterResponseDTO();
        response.setId(COVER_LETTER_ID);
        response.setTitle(request.getTitle());
        response.setFilePath(request.getFilePath());
        response.setContent(request.getContent());

        when(service.create(any(CoverLetterCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/cover-letters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(COVER_LETTER_ID.toString()))
            .andExpect(jsonPath("$.title").value("Product Engineer Letter"));
    }

    @Test
    void deleteRemovesCoverLetter() throws Exception {
        doNothing().when(service).delete(COVER_LETTER_ID);

        mockMvc.perform(delete("/api/cover-letters/{id}", COVER_LETTER_ID))
            .andExpect(status().isNoContent());
    }
}
