package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.JobApplicationResponseDTO;
import com.nick.job_application_tracker.service.JobApplicationService;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @PostMapping
    public ResponseEntity<JobApplicationResponseDTO> create(@RequestBody JobApplicationCreateDTO dto) {
        return ResponseEntity.ok(jobApplicationService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponseDTO>> getAll() {
        return ResponseEntity.ok(jobApplicationService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationDetailDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(jobApplicationService.getById(id));
    }
}
