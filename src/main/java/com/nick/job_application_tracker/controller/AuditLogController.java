package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.service.AuditLogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogDTO>> getAll() {
        List<AuditLogDTO> logs = service.findAll();
        return ResponseEntity.ok(logs);
    }

    @PostMapping
    public ResponseEntity<AuditLogDTO> create(@Valid @RequestBody AuditLogDTO dto) {
        AuditLogDTO saved = service.save(dto);
        return ResponseEntity.status(201).body(saved); 
    }
}
