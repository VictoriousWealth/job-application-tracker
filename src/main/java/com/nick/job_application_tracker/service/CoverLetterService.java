package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.mapper.CoverLetterMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.CoverLetterRepository;

@Service
public class CoverLetterService {

    private final CoverLetterRepository repo;
    private final CoverLetterMapper mapper;
    private final AuditLogService auditLogService;

    public CoverLetterService(CoverLetterRepository repo, CoverLetterMapper mapper, AuditLogService auditLogService) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<CoverLetterDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public CoverLetterDTO save(CoverLetterDTO dto) {
        boolean isNew = (dto.getId() == null);

        CoverLetter entity = mapper.toEntity(dto);
        CoverLetter saved = repo.save(entity);

        if (isNew) {
            auditLogService.logCreate("Created new cover letter with id: " + saved.getId());
        } else {
            auditLogService.logUpdate("Updated cover letter with id: " + saved.getId());
        }

        return mapper.toDTO(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted cover letter with id: " + id);
    }
}
