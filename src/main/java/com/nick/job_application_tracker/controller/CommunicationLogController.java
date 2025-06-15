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

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.service.CommunicationLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/communications")
@Tag(name = "Communication Logs", description = "Track email, call, LinkedIn, and in-person interactions per application")
public class CommunicationLogController {

    private final CommunicationLogService service;

    public CommunicationLogController(CommunicationLogService service) {
        this.service = service;
    }

    @Operation(summary = "Get all communication logs for a job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of communication logs retrieved",
            content = @Content(schema = @Schema(implementation = CommunicationLogDTO.class))),
        @ApiResponse(responseCode = "404", description = "No communication logs found for job ID")
    })
    @GetMapping("/job/{jobAppId}")
    public List<CommunicationLogDTO> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId);
    }

    @Operation(summary = "Create a new communication log")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Communication log created",
            content = @Content(schema = @Schema(implementation = CommunicationLogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public CommunicationLogDTO create(@RequestBody CommunicationLogDTO dto) {
        return service.save(dto);
    }

    @Operation(summary = "Delete a communication log by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Communication log not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
