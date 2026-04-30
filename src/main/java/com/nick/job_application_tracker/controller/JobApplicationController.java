package com.nick.job_application_tracker.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.nick.job_application_tracker.dto.create.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.response.JobApplicationResponseDTO;
import com.nick.job_application_tracker.dto.update.JobApplicationUpdateDTO;
import com.nick.job_application_tracker.service.implementation.JobApplicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/job-applications")
@Tag(name = "Job Applications", description = "Manage job applications and related details")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }



    // ################################################################
    // CREATE
    // ################################################################

    @Operation(summary = "Create a new job application")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Job application created successfully",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    /*
     * This method allows the creation of a job application trhoguh 
     * the use of a JobApplicationCreateDTO and returns a 
     * JobApplicationResponseDTO
     */
    @PostMapping
    public ResponseEntity<JobApplicationResponseDTO> create(@Valid @RequestBody JobApplicationCreateDTO dto) {
        return ResponseEntity.status(201).body(jobApplicationService.create(dto));
    }



    // ################################################################
    // READ
    // ################################################################

    @Operation(summary = "Get detailed information for a specific job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detailed job application info",
            content = @Content(schema = @Schema(implementation = JobApplicationDetailDTO.class))),
        @ApiResponse(responseCode = "404", description = "Job application not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    /*
     * This method acepts a uuid id and retrieves a job application details 
     * in the form of a JobApplicationDetailDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDetailDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.status(200).body(jobApplicationService.getDetailById(id));
    }


    @Operation(summary = "Get all job applications for the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of job applications",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class))),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    /**
     * 
     * @return a list of JobApplicationResponseDTO
     */
    @GetMapping
    public ResponseEntity<Page<JobApplicationResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(jobApplicationService.getAll(pageable));
    }



    // ################################################################
    // UPDATE
    // ################################################################

    @Operation(summary = "Patch a set of all details of a job application for the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job application has been successfully patched",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Job application not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    /**
     * 
     * @return a JobApplicationDetailDTO
     */
    @PatchMapping("/{id}")
    public ResponseEntity<JobApplicationDetailDTO> patchById(@PathVariable UUID id, @RequestBody JsonNode node) {
        return ResponseEntity.ok(jobApplicationService.patchById(id, node));
    }

    @Operation(summary = "Update all details of a job application for the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job application has been successfully updated",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Job application not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    /**
     * 
     * @return a JobApplicationDetailDTO
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobApplicationDetailDTO> updateById(@PathVariable UUID id, @RequestBody JobApplicationUpdateDTO dto) {
        return ResponseEntity.ok(jobApplicationService.updateById(id, dto));
    }



    // ################################################################
    // DELETE
    // ################################################################

    @Operation(summary = "Patch a set of all details of a job application for the current user")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Job application has been soft-deleted",
            content = @Content(schema = @Schema(implementation = JobApplicationResponseDTO.class))),        @ApiResponse(responseCode = "404", description = "Job application not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    /**
     * 
     * @return No content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(@PathVariable UUID id) {
        return ResponseEntity.status(204).body(jobApplicationService.deleteById(id));
    }
}
