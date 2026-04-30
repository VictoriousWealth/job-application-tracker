package com.nick.job_application_tracker.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.response.AnalyticsResponseDTO;
import com.nick.job_application_tracker.dto.response.DashboardResponseDTO;
import com.nick.job_application_tracker.dto.response.RecommendationResponseDTO;
import com.nick.job_application_tracker.service.implementation.WorkspaceInsightsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/insights")
@Tag(name = "Insights", description = "Dashboarding, analytics, and recommendation workflows")
public class InsightsController {

    private final WorkspaceInsightsService workspaceInsightsService;

    public InsightsController(WorkspaceInsightsService workspaceInsightsService) {
        this.workspaceInsightsService = workspaceInsightsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard summaries by status, source, location, and time period")
    public ResponseEntity<DashboardResponseDTO> getDashboard(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until
    ) {
        return ResponseEntity.ok(workspaceInsightsService.getDashboard(since, until));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get pipeline conversion analytics and source performance")
    public ResponseEntity<AnalyticsResponseDTO> getAnalytics(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate since,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate until
    ) {
        return ResponseEntity.ok(workspaceInsightsService.getAnalytics(since, until));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get follow-up, document reuse, and next best action recommendations")
    public ResponseEntity<RecommendationResponseDTO> getRecommendations() {
        return ResponseEntity.ok(workspaceInsightsService.getRecommendations());
    }
}
