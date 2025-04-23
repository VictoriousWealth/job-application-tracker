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

import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.service.JobSourceService;

@RestController
@RequestMapping("/api/job-sources")
public class JobSourceController {

    private final JobSourceService service;

    public JobSourceController(JobSourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<JobSource> getAll() {
        return service.findAll();
    }

    @PostMapping
    public JobSource create(@RequestBody JobSource source) {
        return service.save(source);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
