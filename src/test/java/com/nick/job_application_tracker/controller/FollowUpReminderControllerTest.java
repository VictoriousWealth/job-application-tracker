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
import com.nick.job_application_tracker.dto.create.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.response.FollowUpReminderResponseDTO;
import com.nick.job_application_tracker.service.implementation.FollowUpReminderService;

@WebMvcTest(
    controllers = FollowUpReminderController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class FollowUpReminderControllerTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000000601");
    private static final UUID REMINDER_ID = UUID.fromString("00000000-0000-0000-0000-000000000602");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FollowUpReminderService service;

    @Test
    void getForJobReturnsReminders() throws Exception {
        FollowUpReminderResponseDTO response = new FollowUpReminderResponseDTO();
        response.setId(REMINDER_ID);
        response.setRemindOn(LocalDateTime.of(2026, 5, 5, 9, 0));
        response.setJobApplicationId(JOB_APP_ID);
        response.setNote("Follow up after interview loop");

        when(service.getByJobAppId(JOB_APP_ID)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/follow-ups/job/{jobAppId}", JOB_APP_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(REMINDER_ID.toString()))
            .andExpect(jsonPath("$[0].note").value("Follow up after interview loop"));
    }

    @Test
    void createReturnsCreatedReminder() throws Exception {
        FollowUpReminderCreateDTO request = new FollowUpReminderCreateDTO();
        request.setRemindOn(LocalDateTime.of(2026, 5, 6, 12, 0));
        request.setNote("Send thank-you note");
        request.setJobApplicationId(JOB_APP_ID);

        FollowUpReminderResponseDTO response = new FollowUpReminderResponseDTO();
        response.setId(REMINDER_ID);
        response.setRemindOn(request.getRemindOn());
        response.setNote(request.getNote());
        response.setJobApplicationId(JOB_APP_ID);

        when(service.create(any(FollowUpReminderCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/follow-ups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(REMINDER_ID.toString()))
            .andExpect(jsonPath("$.jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    void deleteRemovesReminder() throws Exception {
        doNothing().when(service).delete(REMINDER_ID);

        mockMvc.perform(delete("/api/follow-ups/{id}", REMINDER_ID))
            .andExpect(status().isNoContent());
    }
}
