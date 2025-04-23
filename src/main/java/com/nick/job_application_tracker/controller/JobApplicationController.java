package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.JobApplicationDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.service.JobApplicationService;

@RestController
@RequestMapping("/api/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    public JobApplicationController(JobApplicationService jobApplicationService) {
        this.jobApplicationService = jobApplicationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<JobApplicationDTO>> getAllByUser(@PathVariable Long userId) {
        List<JobApplicationDTO> dtos = jobApplicationService
            .getAllApplicationsForUser(userId)
            .stream()
            .map(app -> new JobApplicationDTO(app.getId(), app.getJobTitle(), app.getCompany(), app.getStatus().name()))
            .toList();

        return ResponseEntity.ok(dtos);
    }


    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getById(@PathVariable Long id) {
        return jobApplicationService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<JobApplication> create(@RequestBody JobApplication jobApplication) {
        return ResponseEntity.ok(jobApplicationService.createOrUpdate(jobApplication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobApplication> update(@PathVariable Long id, @RequestBody JobApplication updated) {
        updated.setId(id);
        return ResponseEntity.ok(jobApplicationService.createOrUpdate(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        jobApplicationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobApplication>> getByStatus(@PathVariable JobApplication.Status status) {
        return ResponseEntity.ok(jobApplicationService.getByStatus(status));
    }
}
