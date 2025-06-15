package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.SkillTrackerDTO;
import com.nick.job_application_tracker.mapper.SkillTrackerMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.service.JobApplicationService;
import com.nick.job_application_tracker.service.SkillTrackerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skill Tracker", description = "Manage skills associated with job applications")
public class SkillTrackerController {

    private final SkillTrackerService service;
    private final JobApplicationService jobApplicationService;

    public SkillTrackerController(SkillTrackerService service, JobApplicationService jobApplicationService) {
        this.service = service;
        this.jobApplicationService = jobApplicationService;
    }

    @Operation(summary = "Get all skills for a specific job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Skills retrieved successfully",
            content = @Content(schema = @Schema(implementation = SkillTrackerDTO.class)))
    })
    @GetMapping("/job/{jobAppId}")
    public List<SkillTrackerDTO> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId)
                      .stream()
                      .map(SkillTrackerMapper::toDTO)
                      .collect(Collectors.toList());
    }

    @Operation(summary = "Add a new skill to a job application")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Skill created successfully",
            content = @Content(schema = @Schema(implementation = SkillTrackerDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public SkillTrackerDTO create(@RequestBody SkillTrackerCreateDTO dto) {
        JobApplication jobApp = jobApplicationService.findById(dto.getJobApplicationId());
        return SkillTrackerMapper.toDTO(service.save(SkillTrackerMapper.toEntity(dto, jobApp)));
    }

    @Operation(summary = "Delete a skill entry by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Skill deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Skill not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
