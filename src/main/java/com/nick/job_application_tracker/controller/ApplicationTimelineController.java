package com.nick.job_application_tracker.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.nick.job_application_tracker.dto.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.dto.special.ErrorResponseDTO;
import com.nick.job_application_tracker.service.inter_face.ApplicationTimelineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/timelines")
@Tag(name = "Application Timeline", description = "Manage timeline events for job applications")
@SecurityRequirement(name = "bearerAuth") // ⬅️ Marks all endpoints as requiring JWT
public class ApplicationTimelineController {

    private final ApplicationTimelineService service;

    public ApplicationTimelineController(ApplicationTimelineService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    @Operation(summary = "Get timeline events for a specific job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved timeline events",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationTimelineCreateDTO.class))),
        @ApiResponse(responseCode = "404", description = "Job application not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<List<ApplicationTimelineCreateDTO>> getForJob(@PathVariable UUID jobAppId) {
        return ResponseEntity.ok(service.getByJobAppId(jobAppId));
    }

    @PostMapping
    @Operation(summary = "Create a new timeline event")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Timeline event created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationTimelineCreateDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ApplicationTimelineCreateDTO> create(@Valid @RequestBody ApplicationTimelineCreateDTO dto) {
        ApplicationTimelineCreateDTO saved = service.save(dto);
        return ResponseEntity
                .created(URI.create("/api/timelines/" + saved.getId()))
                .body(saved);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a timeline event by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Timeline event deleted"),
        @ApiResponse(responseCode = "404", description = "Timeline event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Replace a timeline event")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Timeline event updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationTimelineCreateDTO.class))),
        @ApiResponse(responseCode = "404", description = "Timeline event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ApplicationTimelineCreateDTO> update(
            @PathVariable UUID id,
            @Valid @RequestBody ApplicationTimelineCreateDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update a timeline event")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Timeline event partially updated",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApplicationTimelineCreateDTO.class))),
        @ApiResponse(responseCode = "404", description = "Timeline event not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    public ResponseEntity<ApplicationTimelineCreateDTO> patch(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(service.patch(id, updates));
    }

}
