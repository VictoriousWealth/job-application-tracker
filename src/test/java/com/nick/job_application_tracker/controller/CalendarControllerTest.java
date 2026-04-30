package com.nick.job_application_tracker.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService.CalendarExportPayload;

@WebMvcTest(
    controllers = CalendarController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class CalendarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CalendarIntegrationService calendarIntegrationService;

    @Test
    void getEventsReturnsCalendarItems() throws Exception {
        when(calendarIntegrationService.getEvents(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31)))
            .thenReturn(List.of(new CalendarEventResponseDTO(
                "FOLLOW_UP_REMINDER",
                com.nick.job_application_tracker.TestIds.uuid(1),
                "OpenAI",
                "Backend Engineer",
                LocalDateTime.of(2026, 5, 3, 9, 0),
                LocalDateTime.of(2026, 5, 3, 9, 15),
                "Follow up: OpenAI - Backend Engineer",
                "Check in with recruiter"
            )));

        mockMvc.perform(get("/api/calendar/events")
                .param("from", "2026-05-01")
                .param("to", "2026-05-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("FOLLOW_UP_REMINDER"))
            .andExpect(jsonPath("$[0].company").value("OpenAI"));
    }

    @Test
    void exportCalendarReturnsIcsAttachment() throws Exception {
        byte[] payload = "BEGIN:VCALENDAR\r\nEND:VCALENDAR\r\n".getBytes(StandardCharsets.UTF_8);
        when(calendarIntegrationService.exportCalendar(null, null))
            .thenReturn(new CalendarExportPayload(payload, "workspace.ics", MediaType.parseMediaType("text/calendar")));

        mockMvc.perform(get("/api/calendar/events.ics"))
            .andExpect(status().isOk())
            .andExpect(header().string("Content-Disposition", "attachment; filename=\"workspace.ics\""))
            .andExpect(content().contentType("text/calendar"))
            .andExpect(content().string("BEGIN:VCALENDAR\r\nEND:VCALENDAR\r\n"));
    }
}
