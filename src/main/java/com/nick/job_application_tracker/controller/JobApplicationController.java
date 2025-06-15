package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.JobApplicationResponseDTO;
import com.nick.job_application_tracker.service.JobApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/job-applications")
@Tag(name = "Job Applications", description = "Manage job applications and related details")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @Operation(summary = "Create a new job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job application created successfully",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<JobApplicationResponseDTO> create(@RequestBody JobApplicationCreateDTO dto) {
        return ResponseEntity.ok(jobApplicationService.create(dto));
    }

    @Operation(summary = "Get all job applications for the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of job applications",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<JobApplicationResponseDTO>> getAll() {
        return ResponseEntity.ok(jobApplicationService.getAll());
    }

    @Operation(summary = "Get detailed information for a specific job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detailed job application info",
            content = @Content(schema = @Schema(implementation = JobApplicationDetailDTO.class))),
        @ApiResponse(responseCode = "404", description = "Job application not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getById(id));
    }
}
