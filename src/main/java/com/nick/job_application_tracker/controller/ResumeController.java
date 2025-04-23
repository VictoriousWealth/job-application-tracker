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

import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.service.ResumeService;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping
    public List<Resume> getAll() {
        return resumeService.findAll();
    }

    @PostMapping
    public Resume upload(@RequestBody Resume resume) {
        return resumeService.save(resume);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resumeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
