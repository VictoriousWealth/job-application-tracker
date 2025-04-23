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

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.service.CommunicationLogService;

@RestController
@RequestMapping("/api/communications")
public class CommunicationLogController {

    private final CommunicationLogService service;

    public CommunicationLogController(CommunicationLogService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public List<CommunicationLogDTO> getForJob(@PathVariable Long jobAppId) {
        return service.getByJobAppId(jobAppId);
    }

    @PostMapping
    public CommunicationLogDTO create(@RequestBody CommunicationLogDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
