package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.service.AuditLogService;

@RestController
@RequestMapping("/api/audit-log")
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping
    public List<AuditLogDTO> getAll() {
        return service.findAll();
    }

    @PostMapping
    public AuditLogDTO create(@RequestBody AuditLogDTO dto) {
        return service.save(dto);
    }
}
