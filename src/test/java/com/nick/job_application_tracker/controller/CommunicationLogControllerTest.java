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
import com.nick.job_application_tracker.dto.create.CommunicationLogCreateDTO;
import com.nick.job_application_tracker.dto.response.CommunicationLogResponseDTO;
import com.nick.job_application_tracker.model.CommunicationLog.Direction;
import com.nick.job_application_tracker.model.CommunicationLog.Method;
import com.nick.job_application_tracker.service.implementation.CommunicationLogService;

@WebMvcTest(
    controllers = CommunicationLogController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class CommunicationLogControllerTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");
    private static final UUID COMMUNICATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000402");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommunicationLogService service;

    @Test
    void getForJobReturnsCommunicationHistory() throws Exception {
        CommunicationLogResponseDTO response = new CommunicationLogResponseDTO();
        response.setId(COMMUNICATION_ID);
        response.setType(Method.EMAIL);
        response.setDirection(Direction.OUTBOUND);
        response.setTimestamp(LocalDateTime.of(2026, 4, 30, 10, 0));
        response.setMessage("Followed up with recruiter");
        response.setJobApplicationId(JOB_APP_ID);

        when(service.getByJobAppId(JOB_APP_ID)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/communications/job/{jobAppId}", JOB_APP_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(COMMUNICATION_ID.toString()))
            .andExpect(jsonPath("$[0].type").value("EMAIL"))
            .andExpect(jsonPath("$[0].direction").value("OUTBOUND"))
            .andExpect(jsonPath("$[0].message").value("Followed up with recruiter"));
    }

    @Test
    void createPersistsCommunicationLog() throws Exception {
        CommunicationLogCreateDTO request = new CommunicationLogCreateDTO();
        request.setType(Method.CALL);
        request.setDirection(Direction.INBOUND);
        request.setTimestamp(LocalDateTime.of(2026, 5, 1, 11, 30));
        request.setMessage("Recruiter called to schedule interview");
        request.setJobApplicationId(JOB_APP_ID);

        CommunicationLogResponseDTO response = new CommunicationLogResponseDTO();
        response.setId(COMMUNICATION_ID);
        response.setType(request.getType());
        response.setDirection(request.getDirection());
        response.setTimestamp(request.getTimestamp());
        response.setMessage(request.getMessage());
        response.setJobApplicationId(JOB_APP_ID);

        when(service.create(any(CommunicationLogCreateDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/communications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(COMMUNICATION_ID.toString()))
            .andExpect(jsonPath("$.type").value("CALL"))
            .andExpect(jsonPath("$.jobApplicationId").value(JOB_APP_ID.toString()));
    }

    @Test
    void deleteRemovesCommunicationLog() throws Exception {
        doNothing().when(service).delete(COMMUNICATION_ID);

        mockMvc.perform(delete("/api/communications/{id}", COMMUNICATION_ID))
            .andExpect(status().isNoContent());
    }
}
