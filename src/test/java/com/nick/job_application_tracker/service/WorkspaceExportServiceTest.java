package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.dto.response.AnalyticsResponseDTO;
import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationItemResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationResponseDTO;
import com.nick.job_application_tracker.dto.response.SourceAnalyticsResponseDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService;
import com.nick.job_application_tracker.service.implementation.WorkspaceExportService;
import com.nick.job_application_tracker.service.implementation.WorkspaceInsightsService;
import com.nick.job_application_tracker.service.implementation.WorkspaceReadService;

class WorkspaceExportServiceTest {

    private WorkspaceReadService workspaceReadService;
    private WorkspaceInsightsService workspaceInsightsService;
    private CalendarIntegrationService calendarIntegrationService;
    private WorkspaceExportService service;

    @BeforeEach
    void setUp() {
        workspaceReadService = mock(WorkspaceReadService.class);
        workspaceInsightsService = mock(WorkspaceInsightsService.class);
        calendarIntegrationService = mock(CalendarIntegrationService.class);
        service = new WorkspaceExportService(
            workspaceReadService,
            workspaceInsightsService,
            calendarIntegrationService,
            new ObjectMapper().findAndRegisterModules()
        );
    }

    @Test
    void exportWorkspaceSupportsJsonCsvAndPdf() {
        User user = new User();
        user.setId(com.nick.job_application_tracker.TestIds.uuid(50));
        user.setEmail("user@example.com");

        JobApplication application = new JobApplication();
        application.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        application.setCompany("OpenAI");
        application.setJobTitle("Backend Engineer");
        application.setStatus(JobApplication.Status.INTERVIEW);
        application.setSource(new JobSource("Referral"));
        application.setLocation(new Location("London", "United Kingdom"));
        application.setCreatedAt(LocalDateTime.of(2026, 4, 1, 10, 0));
        application.setAppliedOn(LocalDateTime.of(2026, 4, 2, 10, 0));
        application.setJobDescription("Distributed systems");

        when(workspaceReadService.getCurrentUser()).thenReturn(user);
        when(workspaceReadService.getJobApplications()).thenReturn(List.of(application));
        when(workspaceReadService.getAttachments()).thenReturn(List.of());
        when(workspaceReadService.getCommunicationLogs()).thenReturn(List.of());
        when(workspaceReadService.getTimelineEntries()).thenReturn(List.of());
        when(workspaceReadService.getSkills()).thenReturn(List.of());
        when(workspaceReadService.getFollowUpReminders()).thenReturn(List.of());
        when(workspaceReadService.getScheduledCommunications()).thenReturn(List.of());
        when(calendarIntegrationService.getEvents(null, null)).thenReturn(List.of(new CalendarEventResponseDTO(
            "SCHEDULED_COMMUNICATION",
            application.getId(),
            "OpenAI",
            "Backend Engineer",
            LocalDateTime.of(2026, 5, 1, 10, 0),
            LocalDateTime.of(2026, 5, 1, 11, 0),
            "Interview: OpenAI - Backend Engineer",
            "Interview loop"
        )));
        when(workspaceInsightsService.getAnalytics(null, null)).thenReturn(new AnalyticsResponseDTO(
            1,
            1,
            1,
            0,
            0,
            100.0,
            0.0,
            0.0,
            100.0,
            0,
            1,
            List.of(new SourceAnalyticsResponseDTO("Referral", 1, 1, 0, 100.0, 0.0))
        ));
        when(workspaceInsightsService.getRecommendations()).thenReturn(new RecommendationResponseDTO(
            LocalDateTime.of(2026, 4, 30, 12, 0),
            List.of(new RecommendationItemResponseDTO(
                "PREPARE_EVENT",
                "HIGH",
                application.getId(),
                "OpenAI",
                "Backend Engineer",
                "Prepare for an upcoming event",
                "Interview scheduled soon.",
                "Review notes."
            ))
        ));

        WorkspaceExportService.ExportPayload json = service.exportWorkspace("json");
        WorkspaceExportService.ExportPayload csv = service.exportWorkspace("csv");
        WorkspaceExportService.ExportPayload pdf = service.exportWorkspace("pdf");

        assertThat(new String(json.content(), StandardCharsets.UTF_8)).contains("\"company\":\"OpenAI\"");
        assertThat(new String(csv.content(), StandardCharsets.UTF_8)).contains("OpenAI");
        assertThat(new String(pdf.content(), StandardCharsets.ISO_8859_1)).startsWith("%PDF-1.4");
    }
}
