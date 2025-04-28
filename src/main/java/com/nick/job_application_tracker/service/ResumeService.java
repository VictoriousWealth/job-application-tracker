package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.mapper.ResumeMapper;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository repo;
    private final AuditLogService auditLogService;

    public ResumeService(ResumeRepository repo, AuditLogService auditLogService) {
        this.repo = repo;
        this.auditLogService = auditLogService;
    }

    public ResumeDTO save(ResumeDTO dto) {
        Resume resume = ResumeMapper.toEntity(dto);
        Resume saved = repo.save(resume);

        if (dto.getId() == null) {
            auditLogService.logCreate("Created resume with id: " + saved.getId());
        } else {
            auditLogService.logUpdate("Updated resume with id: " + saved.getId());
        }

        return ResumeMapper.toDTO(saved);
    }

    public List<ResumeDTO> getAll() {
        return repo.findAll().stream()
            .map(ResumeMapper::toDTO)
            .toList();
    }

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted resume with id: " + id);
    }
}
