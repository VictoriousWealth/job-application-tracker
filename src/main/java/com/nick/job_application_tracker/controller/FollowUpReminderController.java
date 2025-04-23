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

import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.service.FollowUpReminderService;

@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpReminderController {

    private final FollowUpReminderService service;

    public FollowUpReminderController(FollowUpReminderService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public List<FollowUpReminder> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId);
    }

    @PostMapping
    public FollowUpReminder create(@RequestBody FollowUpReminder reminder) {
        return service.save(reminder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
