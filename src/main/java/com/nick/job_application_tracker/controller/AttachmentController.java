package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.dto.ErrorResponseDTO;
import com.nick.job_application_tracker.service.AttachmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attachments")
@Tag(name = "Attachments", description = "Manage file attachments for job applications")
public class AttachmentController {

    private final AttachmentService service;

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @Operation(summary = "Get attachments for a specific job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of attachments retrieved",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttachmentDTO.class))),
        @ApiResponse(responseCode = "404", description = "No attachments found for the given job application",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/job/{jobAppId}")
    public ResponseEntity<List<AttachmentDTO>> getForJob(@PathVariable Long jobAppId) {
        List<AttachmentDTO> attachments = service.getByJobAppId(jobAppId);
        if (attachments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attachments);
    }

    @Operation(summary = "Create a new attachment")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Attachment successfully created",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AttachmentDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDTO create(@Valid @RequestBody AttachmentDTO dto) {
        return service.save(dto);
    }

    @Operation(summary = "Delete an attachment by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Attachment successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Attachment not found",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
