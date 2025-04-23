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

import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.service.ScheduledCommunicationService;

@RestController
@RequestMapping("/api/scheduled-events")
public class ScheduledCommunicationController {

    private final ScheduledCommunicationService service;

    public ScheduledCommunicationController(ScheduledCommunicationService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public List<ScheduledCommunication> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId);
    }

    @PostMapping
    public ScheduledCommunication create(@RequestBody ScheduledCommunication event) {
        return service.save(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
