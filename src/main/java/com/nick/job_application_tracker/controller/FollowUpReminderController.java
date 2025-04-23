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

import com.nick.job_application_tracker.dto.FollowUpReminderCreateDTO;
import com.nick.job_application_tracker.dto.FollowUpReminderDTO;
import com.nick.job_application_tracker.service.FollowUpReminderService;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpReminderController {

    private final FollowUpReminderService service;

    public FollowUpReminderController(FollowUpReminderService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public ResponseEntity<List<FollowUpReminderDTO>> getForJob(@PathVariable Long jobAppId) {
        return ResponseEntity.ok(service.getByJobAppId(jobAppId));
    }

    @PostMapping
    public ResponseEntity<FollowUpReminderDTO> create(@RequestBody FollowUpReminderCreateDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
