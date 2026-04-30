package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.response.AnalyticsResponseDTO;
import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.dto.response.DashboardResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationResponseDTO;
import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.service.implementation.CalendarIntegrationService;
import com.nick.job_application_tracker.service.implementation.WorkspaceInsightsService;
import com.nick.job_application_tracker.service.implementation.WorkspaceReadService;

class WorkspaceInsightsServiceTest {

    private WorkspaceReadService workspaceReadService;
    private CalendarIntegrationService calendarIntegrationService;
    private WorkspaceInsightsService service;

    @BeforeEach
    void setUp() {
        workspaceReadService = mock(WorkspaceReadService.class);
        calendarIntegrationService = mock(CalendarIntegrationService.class);
        service = new WorkspaceInsightsService(workspaceReadService, calendarIntegrationService);
    }

    @Test
    void dashboardBuildsStatusSourceAndLocationBreakdowns() {
        JobApplication applied = application("OpenAI", "Backend Engineer", JobApplication.Status.APPLIED, "Referral", "London", "United Kingdom", LocalDateTime.of(2026, 4, 10, 9, 0));
        JobApplication interview = application("Anthropic", "Platform Engineer", JobApplication.Status.INTERVIEW, "Referral", "London", "United Kingdom", LocalDateTime.of(2026, 4, 12, 9, 0));
        JobApplication offer = application("Stripe", "API Engineer", JobApplication.Status.OFFER, "Company Site", "Remote", "United Kingdom", LocalDateTime.of(2026, 4, 15, 9, 0));

        when(workspaceReadService.getJobApplications()).thenReturn(List.of(applied, interview, offer));
        when(calendarIntegrationService.getEvents(LocalDate.now(), LocalDate.now().plusDays(30)))
            .thenReturn(List.of(new CalendarEventResponseDTO(
                "SCHEDULED_COMMUNICATION",
                com.nick.job_application_tracker.TestIds.uuid(1),
                "OpenAI",
                "Backend Engineer",
                LocalDateTime.of(2026, 5, 1, 10, 0),
                LocalDateTime.of(2026, 5, 1, 11, 0),
                "Interview: OpenAI - Backend Engineer",
                "Interview loop"
            )));

        DashboardResponseDTO dashboard = service.getDashboard(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertThat(dashboard.totalApplications()).isEqualTo(3);
        assertThat(dashboard.applicationsInPeriod()).isEqualTo(3);
        assertThat(dashboard.activeApplications()).isEqualTo(2);
        assertThat(dashboard.closedApplications()).isEqualTo(1);
        assertThat(dashboard.statusBreakdown()).containsEntry("APPLIED", 1L).containsEntry("INTERVIEW", 1L).containsEntry("OFFER", 1L);
        assertThat(dashboard.sourceBreakdown()).containsEntry("Referral", 2L);
        assertThat(dashboard.upcomingEvents()).hasSize(1);
    }

    @Test
    void analyticsAndRecommendationsReflectPipelineState() {
        JobApplication applied = application("OpenAI", "Backend Engineer", JobApplication.Status.APPLIED, "Referral", "London", "United Kingdom", LocalDateTime.now().minusDays(10));
        JobApplication interview = application("Anthropic", "Platform Engineer", JobApplication.Status.INTERVIEW, "Referral", "London", "United Kingdom", LocalDateTime.now().minusDays(3));

        FollowUpReminder overdueReminder = new FollowUpReminder();
        overdueReminder.setJobApplication(applied);
        overdueReminder.setRemindOn(LocalDateTime.now().minusDays(1));
        overdueReminder.setNote("Check in");

        ScheduledCommunication upcomingInterview = new ScheduledCommunication();
        upcomingInterview.setJobApplication(interview);
        upcomingInterview.setType(ScheduledCommunication.Type.INTERVIEW);
        upcomingInterview.setScheduledFor(LocalDateTime.now().plusHours(12));
        upcomingInterview.setNotes("Virtual onsite");

        Resume resume = new Resume("Backend Resume", "/files/backend-resume.pdf");

        when(workspaceReadService.getJobApplications()).thenReturn(List.of(applied, interview));
        when(workspaceReadService.getFollowUpReminders()).thenReturn(List.of(overdueReminder));
        when(workspaceReadService.getScheduledCommunications()).thenReturn(List.of(upcomingInterview));
        when(workspaceReadService.getResumes()).thenReturn(List.of(resume));
        when(workspaceReadService.getCoverLetters()).thenReturn(List.of());

        AnalyticsResponseDTO analytics = service.getAnalytics(null, null);
        RecommendationResponseDTO recommendations = service.getRecommendations();

        assertThat(analytics.appliedCount()).isEqualTo(2);
        assertThat(analytics.interviewStageCount()).isEqualTo(1);
        assertThat(analytics.overdueReminders()).isEqualTo(1);
        assertThat(recommendations.items()).extracting(item -> item.type())
            .contains("FOLLOW_UP_DUE", "PREPARE_EVENT", "DOCUMENT_REUSE", "FOLLOW_UP_SUGGESTED");
    }

    private JobApplication application(
        String company,
        String jobTitle,
        JobApplication.Status status,
        String source,
        String city,
        String country,
        LocalDateTime appliedOn
    ) {
        JobApplication application = new JobApplication();
        application.setId(com.nick.job_application_tracker.TestIds.uuid(company.hashCode() & 0xffff));
        application.setCompany(company);
        application.setJobTitle(jobTitle);
        application.setStatus(status);
        application.setSource(new JobSource(source));
        application.setLocation(new Location(city, country));
        application.setJobDescription("Build backend systems");
        application.setAppliedOn(appliedOn);
        application.setCreatedAt(appliedOn.minusDays(1));
        return application;
    }
}
