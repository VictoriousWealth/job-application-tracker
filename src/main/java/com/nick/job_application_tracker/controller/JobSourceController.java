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

import com.nick.job_application_tracker.dto.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.service.JobSourceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/job-sources")
public class JobSourceController {

    private final JobSourceService jobSourceService;

    public JobSourceController(JobSourceService jobSourceService) {
        this.jobSourceService = jobSourceService;
    }

    @GetMapping
    public ResponseEntity<List<JobSourceDTO>> getAllSources() {
        return ResponseEntity.ok(jobSourceService.getAllSources());
    }

    @PostMapping
    public ResponseEntity<JobSourceDTO> createSource(@Valid @RequestBody JobSourceCreateDTO createDTO) {
        JobSourceDTO created = jobSourceService.createSource(createDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobSourceDTO> getSourceById(@PathVariable Long id) {
        return jobSourceService.getSourceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobSourceDTO> updateSource(@PathVariable Long id, @Valid @RequestBody JobSourceCreateDTO updateDTO) {
        return jobSourceService.updateSource(id, updateDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSource(@PathVariable Long id) {
        jobSourceService.deleteSource(id);
        return ResponseEntity.noContent().build();
    }
}
