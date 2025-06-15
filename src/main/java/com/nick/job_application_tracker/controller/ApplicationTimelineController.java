package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.service.ApplicationTimelineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/timelines")
@Tag(name = "Application Timeline", description = "Manage timeline events for job applications")
public class ApplicationTimelineController {

    private final ApplicationTimelineService service;

    public ApplicationTimelineController(ApplicationTimelineService service) {
        this.service = service;
    }

    @Operation(summary = "Get timeline events for a specific job application")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved timeline events",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationTimelineDTO.class)))
    @GetMapping("/job/{jobAppId}")
    public ResponseEntity<List<ApplicationTimelineDTO>> getForJob(@PathVariable Long jobAppId) {
        return ResponseEntity.ok(service.getByJobAppId(jobAppId));
    }

    @Operation(summary = "Create a new timeline event")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Timeline event created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationTimelineDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = com.nick.job_application_tracker.dto.ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<ApplicationTimelineDTO> create(@Valid @RequestBody ApplicationTimelineDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @Operation(summary = "Delete a timeline event by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Timeline event deleted"),
        @ApiResponse(responseCode = "404", description = "Timeline event not found",
            content = @Content(schema = @Schema(implementation = com.nick.job_application_tracker.dto.ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
