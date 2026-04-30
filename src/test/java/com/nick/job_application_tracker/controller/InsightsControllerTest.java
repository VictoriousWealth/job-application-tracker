package com.nick.job_application_tracker.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.dto.response.AnalyticsResponseDTO;
import com.nick.job_application_tracker.dto.response.CalendarEventResponseDTO;
import com.nick.job_application_tracker.dto.response.DashboardResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationItemResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationResponseDTO;
import com.nick.job_application_tracker.dto.response.SourceAnalyticsResponseDTO;
import com.nick.job_application_tracker.service.implementation.WorkspaceInsightsService;

@WebMvcTest(
    controllers = InsightsController.class,
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CustomJwtAuthFilter.class)
)
@AutoConfigureMockMvc(addFilters = false)
class InsightsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkspaceInsightsService workspaceInsightsService;

    @Test
    void dashboardReturnsSummaryPayload() throws Exception {
        DashboardResponseDTO response = new DashboardResponseDTO(
            LocalDate.of(2026, 4, 1),
            LocalDate.of(2026, 4, 30),
            8,
            4,
            3,
            1,
            Map.of("APPLIED", 2L, "INTERVIEW", 1L, "OFFER", 1L),
            Map.of("Referral", 2L),
            Map.of("London, United Kingdom", 4L),
            Map.of("2026-04-10", 2L),
            List.of(new CalendarEventResponseDTO(
                "SCHEDULED_COMMUNICATION",
                com.nick.job_application_tracker.TestIds.uuid(1),
                "OpenAI",
                "Backend Engineer",
                LocalDateTime.of(2026, 5, 2, 10, 0),
                LocalDateTime.of(2026, 5, 2, 11, 0),
                "Interview: OpenAI - Backend Engineer",
                "System design interview"
            ))
        );

        when(workspaceInsightsService.getDashboard(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30))).thenReturn(response);

        mockMvc.perform(get("/api/insights/dashboard")
                .param("since", "2026-04-01")
                .param("until", "2026-04-30"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalApplications").value(8))
            .andExpect(jsonPath("$.statusBreakdown.APPLIED").value(2))
            .andExpect(jsonPath("$.upcomingEvents[0].company").value("OpenAI"));
    }

    @Test
    void analyticsReturnsConversionMetrics() throws Exception {
        AnalyticsResponseDTO response = new AnalyticsResponseDTO(
            6,
            5,
            2,
            1,
            1,
            40.0,
            50.0,
            20.0,
            66.7,
            1,
            2,
            List.of(new SourceAnalyticsResponseDTO("Referral", 3, 2, 1, 66.7, 33.3))
        );

        when(workspaceInsightsService.getAnalytics(null, null)).thenReturn(response);

        mockMvc.perform(get("/api/insights/analytics"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.appliedToInterviewRate").value(40.0))
            .andExpect(jsonPath("$.sourcePerformance[0].sourceName").value("Referral"));
    }

    @Test
    void recommendationsReturnsActionItems() throws Exception {
        RecommendationResponseDTO response = new RecommendationResponseDTO(
            LocalDateTime.of(2026, 4, 30, 12, 0),
            List.of(new RecommendationItemResponseDTO(
                "FOLLOW_UP_DUE",
                "HIGH",
                com.nick.job_application_tracker.TestIds.uuid(10),
                "OpenAI",
                "Backend Engineer",
                "Follow-up is overdue",
                "The reminder is overdue.",
                "Send the follow-up."
            ))
        );

        when(workspaceInsightsService.getRecommendations()).thenReturn(response);

        mockMvc.perform(get("/api/insights/recommendations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.items[0].type").value("FOLLOW_UP_DUE"))
            .andExpect(jsonPath("$.items[0].priority").value("HIGH"));
    }
}
