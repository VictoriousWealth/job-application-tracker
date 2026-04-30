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

import org.junit.jupiter.api.DisplayName;
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
import com.nick.job_application_tracker.dto.create.AttachmentCreateDTO;
import com.nick.job_application_tracker.dto.response.AttachmentResponseDTO;
import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.service.implementation.AttachmentService;

@WebMvcTest(
    controllers = AttachmentController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class AttachmentControllerTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    private static final UUID ATTACHMENT_ID = UUID.fromString("00000000-0000-0000-0000-000000000201");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AttachmentService service;

    @Test
    @DisplayName("Returns attachments for a job application")
    void shouldReturnAttachmentsWhenTheyExist() throws Exception {
        AttachmentResponseDTO dto = new AttachmentResponseDTO();
        dto.setId(ATTACHMENT_ID);
        dto.setType(Attachment.Type.JOB_DESCRIPTION);
        dto.setFilePath("/files/offer.pdf");
        dto.setJobApplicationId(JOB_APP_ID);

        when(service.getByJobAppId(JOB_APP_ID)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/attachments/job/{jobAppId}", JOB_APP_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(ATTACHMENT_ID.toString()))
            .andExpect(jsonPath("$[0].type").value("JOB_DESCRIPTION"))
            .andExpect(jsonPath("$[0].jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    @DisplayName("Returns 404 when no attachments exist")
    void shouldReturn404WhenNoAttachmentsFound() throws Exception {
        when(service.getByJobAppId(JOB_APP_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/attachments/job/{jobAppId}", JOB_APP_ID))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Creates a new attachment")
    void shouldCreateAttachmentWhenValid() throws Exception {
        AttachmentCreateDTO request = new AttachmentCreateDTO();
        request.setType(Attachment.Type.JOB_DESCRIPTION);
        request.setFilePath("/files/cv.pdf");
        request.setJobApplicationId(JOB_APP_ID);

        AttachmentResponseDTO response = new AttachmentResponseDTO();
        response.setId(ATTACHMENT_ID);
        response.setType(Attachment.Type.JOB_DESCRIPTION);
        response.setFilePath("/files/cv.pdf");
        response.setJobApplicationId(JOB_APP_ID);

        when(service.create(any(AttachmentCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(ATTACHMENT_ID.toString()))
            .andExpect(jsonPath("$.type").value("JOB_DESCRIPTION"))
            .andExpect(jsonPath("$.jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    @DisplayName("Returns 400 for malformed UUID path variables")
    void shouldReturn400ForInvalidJobAppId() throws Exception {
        mockMvc.perform(get("/api/attachments/job/invalid-id"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deletes an attachment")
    void shouldDeleteAttachmentSuccessfully() throws Exception {
        doNothing().when(service).delete(ATTACHMENT_ID);

        mockMvc.perform(delete("/api/attachments/{id}", ATTACHMENT_ID))
            .andExpect(status().isNoContent());
    }
}
