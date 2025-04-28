package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.mapper.JobSourceMapper;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.JobSourceRepository;

@Service
public class JobSourceService {

    private final JobSourceRepository repo;
    private final JobSourceMapper mapper;
    private final AuditLogService auditLogService;

    public JobSourceService(JobSourceRepository repo, JobSourceMapper mapper, AuditLogService auditLogService) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<JobSourceDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public JobSourceDTO createSource(JobSourceDTO dto) {
        JobSource source = mapper.toEntity(dto);
        JobSource saved = repo.save(source);

        auditLogService.logCreate("Created new job source: " + saved.getName());
        return mapper.toDTO(saved);
    }

    public JobSourceDTO updateSource(Long id, JobSourceDTO dto) {
        JobSource existing = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job Source not found with id: " + id));

        existing.setName(dto.getName());
        JobSource updated = repo.save(existing);

        auditLogService.logUpdate("Updated job source ID: " + id);
        return mapper.toDTO(updated);
    }

    public void deleteSource(Long id) {
        JobSource existing = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Job Source not found with id: " + id));

        repo.delete(existing);
        auditLogService.logDelete("Deleted job source ID: " + id);
    }
}
