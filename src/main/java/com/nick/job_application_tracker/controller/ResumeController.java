package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.create.ResumeCreateDTO;
import com.nick.job_application_tracker.dto.response.ResumeResponseDTO;
import com.nick.job_application_tracker.service.implementation.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/resumes")
@Tag(name = "Resumes", description = "Manage user-uploaded resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @Operation(summary = "Get all resumes")
    @ApiResponse(responseCode = "200", description = "List of resumes",
        content = @Content(schema = @Schema(implementation = ResumeResponseDTO.class)))
    @GetMapping
    public List<ResumeResponseDTO> getAll() {
        return resumeService.findAll();
    }

    @Operation(summary = "Upload a new resume")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Resume uploaded successfully",
            content = @Content(schema = @Schema(implementation = ResumeResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ResumeResponseDTO> upload(@Valid @RequestBody ResumeCreateDTO resumeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resumeService.create(resumeDTO));
    }

    @Operation(summary = "Delete a resume by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resume deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        resumeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
