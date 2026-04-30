package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.create.CoverLetterCreateDTO;
import com.nick.job_application_tracker.dto.response.CoverLetterResponseDTO;
import com.nick.job_application_tracker.service.implementation.CoverLetterService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/cover-letters")
@Tag(name = "Cover Letters", description = "Manage user-uploaded cover letters")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    public CoverLetterController(CoverLetterService service) {
        this.coverLetterService = service;
    }

    @Operation(summary = "Get all cover letters for the current user")
    @ApiResponse(responseCode = "200", description = "List of cover letters",
        content = @Content(schema = @Schema(implementation = CoverLetterResponseDTO.class)))
    @GetMapping
    public List<CoverLetterResponseDTO> getAll() {
        return coverLetterService.findAll();
    }

    @Operation(summary = "Upload or save a new cover letter")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cover letter saved",
            content = @Content(schema = @Schema(implementation = CoverLetterResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    public ResponseEntity<CoverLetterResponseDTO> save(@RequestBody CoverLetterCreateDTO dto) {
        return ResponseEntity.ok(coverLetterService.create(dto));
    }

    @Operation(summary = "Delete a cover letter by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Cover letter not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        coverLetterService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
