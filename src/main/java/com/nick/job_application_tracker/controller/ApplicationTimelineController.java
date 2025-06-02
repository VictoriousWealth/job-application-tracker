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

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.service.ApplicationTimelineService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/timelines")
public class ApplicationTimelineController {

    private final ApplicationTimelineService service;

    public ApplicationTimelineController(ApplicationTimelineService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public ResponseEntity<List<ApplicationTimelineDTO>> getForJob(@PathVariable Long jobAppId) {
        return ResponseEntity.ok(service.getByJobAppId(jobAppId));
    }

    @PostMapping
    public ResponseEntity<ApplicationTimelineDTO> create(@Valid @RequestBody ApplicationTimelineDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


}
