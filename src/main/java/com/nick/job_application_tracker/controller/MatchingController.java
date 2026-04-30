package com.nick.job_application_tracker.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.response.ApplicationMatchResponseDTO;
import com.nick.job_application_tracker.service.implementation.ApplicationMatchingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/matching")
@Tag(name = "Matching", description = "AI-assisted matching and fit guidance endpoints")
public class MatchingController {

    private final ApplicationMatchingService applicationMatchingService;

    public MatchingController(ApplicationMatchingService applicationMatchingService) {
        this.applicationMatchingService = applicationMatchingService;
    }

    @GetMapping("/applications/{id}")
    @Operation(summary = "Compare a job application against stored resumes, cover letters, and tracked skills")
    public ResponseEntity<ApplicationMatchResponseDTO> matchApplication(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationMatchingService.matchApplication(id));
    }
}
