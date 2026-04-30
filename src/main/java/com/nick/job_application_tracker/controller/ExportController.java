package com.nick.job_application_tracker.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.service.implementation.WorkspaceExportService;
import com.nick.job_application_tracker.service.implementation.WorkspaceExportService.ExportPayload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/exports")
@Tag(name = "Exports", description = "Workspace export endpoints for JSON, CSV, and PDF")
public class ExportController {

    private final WorkspaceExportService workspaceExportService;

    public ExportController(WorkspaceExportService workspaceExportService) {
        this.workspaceExportService = workspaceExportService;
    }

    @GetMapping("/workspace")
    @Operation(summary = "Export the current user's workspace history as JSON, CSV, or PDF")
    public ResponseEntity<byte[]> exportWorkspace(@RequestParam(defaultValue = "json") String format) {
        ExportPayload payload = workspaceExportService.exportWorkspace(format);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.filename() + "\"")
            .contentType(payload.contentType())
            .body(payload.content());
    }
}
