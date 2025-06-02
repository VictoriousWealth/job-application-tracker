package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.AttachmentDTO;
import com.nick.job_application_tracker.service.AttachmentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/attachments")
public class AttachmentController {

    private final AttachmentService service;    

    public AttachmentController(AttachmentService service) {
        this.service = service;
    }

    @GetMapping("/job/{jobAppId}")
    public ResponseEntity<List<AttachmentDTO>> getForJob(@PathVariable Long jobAppId) {
        List<AttachmentDTO> attachments = service.getByJobAppId(jobAppId);
        if (attachments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(attachments);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDTO create(@Valid @RequestBody AttachmentDTO dto) {
        return service.save(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
