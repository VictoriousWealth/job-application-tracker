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

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.service.CoverLetterService;


@RestController
@RequestMapping("/api/cover-letters")
public class CoverLetterController {

    private final CoverLetterService coverLetterService;

    public CoverLetterController(CoverLetterService service) {
        this.coverLetterService = service;
    }

    @GetMapping
    public List<CoverLetterDTO> getAll() {
        return coverLetterService.findAll();
    }

    @PostMapping
    public ResponseEntity<CoverLetterDTO> save(@RequestBody CoverLetterDTO dto) {
        return ResponseEntity.ok(coverLetterService.save(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        coverLetterService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
