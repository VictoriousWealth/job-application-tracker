package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nick.job_application_tracker.dto.SkillTrackerDTO;
import com.nick.job_application_tracker.dto.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.mapper.SkillTrackerMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.service.JobApplicationService;
import com.nick.job_application_tracker.service.SkillTrackerService;

@RestController
@RequestMapping("/api/skills")
public class SkillTrackerController {

    private final SkillTrackerService service;
    private final JobApplicationService jobApplicationService;

    public SkillTrackerController(SkillTrackerService service, JobApplicationService jobApplicationService) {
        this.service = service;
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/job/{jobAppId}")
    public List<SkillTrackerDTO> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId)
                      .stream()
                      .map(SkillTrackerMapper::toDTO)
                      .collect(Collectors.toList());
    }

    @PostMapping
    public SkillTrackerDTO create(@RequestBody SkillTrackerCreateDTO dto) {
        JobApplication jobApp = jobApplicationService.findById(dto.getJobApplicationId());
        return SkillTrackerMapper.toDTO(service.save(SkillTrackerMapper.toEntity(dto, jobApp)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
