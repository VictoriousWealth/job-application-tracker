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

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.service.ResumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
        content = @Content(schema = @Schema(implementation = ResumeDTO.class)))
    @GetMapping
    public List<ResumeDTO> getAll() {
        return resumeService.findAll();
    }

    @Operation(summary = "Upload a new resume")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resume uploaded successfully",
            content = @Content(schema = @Schema(implementation = ResumeDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResumeDTO upload(@RequestBody ResumeDTO resumeDTO) {
        return resumeService.save(resumeDTO);
    }

    @Operation(summary = "Delete a resume by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Resume deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Resume not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resumeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
