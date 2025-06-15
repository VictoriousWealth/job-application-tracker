package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.dto.ErrorResponseDTO;
import com.nick.job_application_tracker.service.AuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/audit-log")
@Tag(name = "Audit Logs", description = "Audit trail for user/system actions")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @Operation(summary = "Get all audit logs")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "List of audit logs retrieved",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AuditLogDTO.class)))
    })
    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAll() {
        List<AuditLogDTO> logs = service.findAll();
        return ResponseEntity.ok(logs);
    }

    @Operation(summary = "Create a new audit log entry")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Audit log entry created successfully",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = AuditLogDTO.class))),
        @ApiResponse(responseCode = "400", description = "Validation error",
            content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping
    public ResponseEntity<AuditLogDTO> create(@Valid @RequestBody AuditLogDTO dto) {
        AuditLogDTO saved = service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
