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

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.service.FollowUpReminderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/follow-ups")
@Tag(name = "Follow-Up Reminders", description = "Manage follow-up reminders linked to job applications")
public class FollowUpReminderController {

    private final FollowUpReminderService service;

    public FollowUpReminderController(FollowUpReminderService service) {
        this.service = service;
    }

    @Operation(summary = "Get all follow-up reminders for a specific job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Follow-up reminders retrieved",
            content = @Content(schema = @Schema(implementation = FollowUpReminderDTO.class))),
        @ApiResponse(responseCode = "404", description = "Job application not found")
    })
    @GetMapping("/job/{jobAppId}")
    public ResponseEntity<List<FollowUpReminderDTO>> getForJob(@PathVariable Long jobAppId) {
        return ResponseEntity.ok(service.getByJobAppId(jobAppId));
    }

    @Operation(summary = "Create a new follow-up reminder")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Reminder created",
            content = @Content(schema = @Schema(implementation = FollowUpReminderDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<FollowUpReminderDTO> create(@RequestBody FollowUpReminderCreateDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @Operation(summary = "Delete a follow-up reminder by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Reminder deleted"),
        @ApiResponse(responseCode = "404", description = "Reminder not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
