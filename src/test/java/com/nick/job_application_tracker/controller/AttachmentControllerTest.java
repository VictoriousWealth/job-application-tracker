package com.nick.job_application_tracker.controller;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.service.AttachmentService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(controllers = AttachmentController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class))
@AutoConfigureMockMvc(addFilters = false)
class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService service;


    @Test
    @DisplayName("200 OK - Should return list of attachments when they exist")
    void shouldReturnAttachmentsWhenTheyExist() throws Exception {
        List<AttachmentDTO> mockList = List.of(
            new AttachmentDTO(1L, "OFFER_LETTER", "/files/offer.pdf", 2L),
            new AttachmentDTO(2L, "JOB_DESCRIPTION", "/files/jobdesc.pdf", 2L)
        );

        Mockito.when(service.getByJobAppId(2L)).thenReturn(mockList);

        mockMvc.perform(get("/api/attachments/job/2"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].type").value("OFFER_LETTER"))
            .andExpect(jsonPath("$[1].filePath").value("/files/jobdesc.pdf"));
    }

    @Test
    @DisplayName("404 Not Found - Should return 404 if no attachments exist for jobAppId")
    void shouldReturn404WhenNoAttachmentsFound() throws Exception {
        Mockito.when(service.getByJobAppId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/attachments/job/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("400 Bad Request - Should return 400 if jobAppId is not a number")
    void shouldReturn400ForInvalidJobAppId() throws Exception {
        mockMvc.perform(get("/api/attachments/job/invalid-id"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("500 Internal Server Error - Should return 500 if service throws unexpected exception")
    void shouldReturn500OnServiceFailure() throws Exception {
        Mockito.when(service.getByJobAppId(2L)).thenThrow(new RuntimeException("Unexpected"));

        mockMvc.perform(get("/api/attachments/job/2"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("Internal error occurred"));
    }


    @Test
    @DisplayName("201 Created - Should create attachment when input is valid")
    void shouldCreateAttachmentWhenValid() throws Exception {
        AttachmentDTO requestDto = new AttachmentDTO(null, "CV", "/files/cv.pdf", 1L);
        AttachmentDTO savedDto = new AttachmentDTO(42L, "CV", "/files/cv.pdf", 1L);

        when(service.save(any(AttachmentDTO.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(42L))
            .andExpect(jsonPath("$.type").value("CV"))
            .andExpect(jsonPath("$.filePath").value("/files/cv.pdf"))
            .andExpect(jsonPath("$.jobApplicationId").value(1));
    }


    @Test
    @DisplayName("400 Bad Request - Should return 400 if required fields are missing")
    void shouldReturn400WhenMissingFields() throws Exception {
        String invalidJson = """
            {
                "filePath": "/files/cv.pdf",
                "jobApplicationId": 1
            }
            """; // Missing "type"

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("400 Bad Request - Should return 400 if ID is provided in request")
    void shouldReturn400WhenIdIsProvided() throws Exception {
        AttachmentDTO dto = new AttachmentDTO(99L, "Transcript", "/files/transcript.pdf", 1L); // ID is not allowed

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }



    @Test
    @DisplayName("400 Bad Request - Should return 400 for malformed JSON")
    void shouldReturn400ForMalformedJson() throws Exception {
        String malformedJson = "{ \"fileName\": \"resume.pdf\", "; // Missing ending

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
            .andExpect(status().isBadRequest())
            .andExpect(content().string(Matchers.containsString("Malformed JSON request")));
    }

    @Test
    @DisplayName("404 Not Found - Should return 404 if jobApplicationId does not exist")
    void shouldReturn404IfJobAppIdInvalid() throws Exception {
        AttachmentDTO dto = new AttachmentDTO(null, "Offer", "/files/offer.pdf", 999L);

        when(service.save(any())).thenThrow(new EntityNotFoundException("Job Application with ID 999 not found"));

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Job Application with ID 999 not found"));
    }


    @Test
    @DisplayName("500 Internal Server Error - Should return 500 if unexpected error occurs during save")
    void shouldReturn500OnUnexpectedErrorDuringCreate() throws Exception {
        AttachmentDTO dto = new AttachmentDTO(null, "CV", "/files/cv.pdf", 1L);
        when(service.save(any())).thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(post("/api/attachments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(dto)))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("Internal error occurred"));
    }


    @Test
    @DisplayName("204 No Content - Should delete attachment when ID is valid")
    void shouldDeleteAttachmentSuccessfully() throws Exception {
        Mockito.doNothing().when(service).delete(10L);

        mockMvc.perform(delete("/api/attachments/10"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("404 Not Found - Should return 404 when deleting non-existent attachment")
    void shouldReturn404WhenDeletingNonExistentAttachment() throws Exception {
        Mockito.doThrow(new EntityNotFoundException("Attachment with ID 999 not found"))
            .when(service).delete(999L);

        mockMvc.perform(delete("/api/attachments/999"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("Attachment with ID 999 not found"));
    }

    @Test
    @DisplayName("500 Internal Server Error - Should return 500 on unexpected error during delete")
    void shouldReturn500OnUnexpectedErrorDuringDelete() throws Exception {
        Mockito.doThrow(new RuntimeException("Unexpected delete failure"))
            .when(service).delete(20L);

        mockMvc.perform(delete("/api/attachments/20"))
            .andExpect(status().isInternalServerError())
            .andExpect(content().string("Internal error occurred"));
    }

}
