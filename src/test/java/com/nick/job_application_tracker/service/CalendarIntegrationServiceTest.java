package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService.CalendarExportPayload;
import com.nick.job_application_tracker.service.implementation.WorkspaceReadService;

class CalendarIntegrationServiceTest {

    private WorkspaceReadService workspaceReadService;
    private CalendarIntegrationService service;

    @BeforeEach
    void setUp() {
        workspaceReadService = mock(WorkspaceReadService.class);
        service = new CalendarIntegrationService(workspaceReadService);
    }

    @Test
    void getEventsAndExportCalendarIncludeSchedulesAndReminders() {
        JobApplication application = new JobApplication();
        application.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        application.setCompany("OpenAI");
        application.setJobTitle("Backend Engineer");

        ScheduledCommunication schedule = new ScheduledCommunication();
        schedule.setJobApplication(application);
        schedule.setType(ScheduledCommunication.Type.INTERVIEW);
        schedule.setScheduledFor(LocalDateTime.of(2026, 5, 10, 10, 0));
        schedule.setNotes("System design");

        FollowUpReminder reminder = new FollowUpReminder();
        reminder.setJobApplication(application);
        reminder.setRemindOn(LocalDateTime.of(2026, 5, 12, 9, 0));
        reminder.setNote("Check in with recruiter");

        when(workspaceReadService.getScheduledCommunications()).thenReturn(List.of(schedule));
        when(workspaceReadService.getFollowUpReminders()).thenReturn(List.of(reminder));

        List<CalendarEventResponseDTO> events = service.getEvents(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));
        CalendarExportPayload export = service.exportCalendar(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 31));
        String ics = new String(export.content(), StandardCharsets.UTF_8);

        assertThat(events).hasSize(2);
        assertThat(events).extracting(CalendarEventResponseDTO::type).contains("SCHEDULED_COMMUNICATION", "FOLLOW_UP_REMINDER");
        assertThat(ics).contains("BEGIN:VCALENDAR");
        assertThat(ics).contains("Interview: OpenAI - Backend Engineer");
        assertThat(ics).contains("Follow up: OpenAI - Backend Engineer");
    }
}
