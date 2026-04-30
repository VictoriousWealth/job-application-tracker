package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.create.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobSourceDetailDTO;
import com.nick.job_application_tracker.dto.response.JobSourceResponseDTO;
import com.nick.job_application_tracker.dto.update.JobSourceUpdateDTO;
import com.nick.job_application_tracker.service.implementation.JobSourceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/job-sources")
@Tag(name = "Job Sources", description = "Manage platforms where jobs are found (LinkedIn, Indeed, etc.)")
public class JobSourceController {

    private final JobSourceService jobSourceService;

    public JobSourceController(JobSourceService jobSourceService) {
        this.jobSourceService = jobSourceService;
    }

    @Operation(summary = "Get all job sources")
    @ApiResponse(responseCode = "200", description = "List of job sources",
        content = @Content(schema = @Schema(implementation = JobSourceResponseDTO.class)))
    @GetMapping
    public ResponseEntity<List<JobSourceResponseDTO>> getAllSources() {
        return ResponseEntity.ok(jobSourceService.getAllSources());
    }

    @Operation(summary = "Create a new job source")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job source created",
            content = @Content(schema = @Schema(implementation = JobSourceResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    @PostMapping
    public ResponseEntity<JobSourceResponseDTO> createSource(@Valid @RequestBody JobSourceCreateDTO createDTO) {
        JobSourceResponseDTO created = jobSourceService.createSource(createDTO);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Get a job source by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job source found",
            content = @Content(schema = @Schema(implementation = JobSourceDetailDTO.class))),
        @ApiResponse(responseCode = "404", description = "Job source not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JobSourceDetailDTO> getSourceById(@PathVariable UUID id) {
        return jobSourceService.getSourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update a job source by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job source updated",
            content = @Content(schema = @Schema(implementation = JobSourceDetailDTO.class))),
        @ApiResponse(responseCode = "404", description = "Job source not found"),
        @ApiResponse(responseCode = "400", description = "Invalid update payload")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JobSourceDetailDTO> updateSource(
            @PathVariable UUID id,
            @Valid @RequestBody JobSourceUpdateDTO updateDTO) {
        return jobSourceService.updateSource(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a job source by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Job source deleted"),
        @ApiResponse(responseCode = "404", description = "Job source not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable UUID id) {
        jobSourceService.deleteSource(id);
        return ResponseEntity.noContent().build();
    }
}
