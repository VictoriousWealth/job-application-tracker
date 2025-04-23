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

import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.service.SkillTrackerService;

@RestController
@RequestMapping("/api/skills")
public class SkillTrackerController {

    private final SkillTrackerService service;

    public SkillTrackerController(SkillTrackerService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public List<SkillTracker> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId);
    }

    @PostMapping
    public SkillTracker create(@RequestBody SkillTracker skill) {
        return service.save(skill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
