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

import com.nick.job_application_tracker.dto.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.ScheduledCommunicationDTO;
import com.nick.job_application_tracker.service.ScheduledCommunicationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/scheduled-communications")
@Tag(name = "Scheduled Communications", description = "Manage interviews, assessments, and other scheduled events")
public class ScheduledCommunicationController {

    private final ScheduledCommunicationService service;

    public ScheduledCommunicationController(ScheduledCommunicationService service) {
        this.service = service;
    }

    @Operation(summary = "Get all scheduled communications")
    @ApiResponse(responseCode = "200", description = "List of scheduled events",
        content = @Content(schema = @Schema(implementation = ScheduledCommunicationDTO.class)))
    @GetMapping
    public ResponseEntity<List<ScheduledCommunicationDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get a scheduled communication by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event found",
            content = @Content(schema = @Schema(implementation = ScheduledCommunicationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ScheduledCommunicationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Create a new scheduled communication")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event created successfully",
            content = @Content(schema = @Schema(implementation = ScheduledCommunicationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ScheduledCommunicationDTO> create(@RequestBody ScheduledCommunicationCreateDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @Operation(summary = "Delete a scheduled communication by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
