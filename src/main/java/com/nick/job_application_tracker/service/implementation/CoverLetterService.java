package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.CoverLetterCreateDTO;
import com.nick.job_application_tracker.dto.response.CoverLetterResponseDTO;
import com.nick.job_application_tracker.mapper.CoverLetterMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.inter_face.CoverLetterRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

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

    public List<CoverLetterResponseDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toResponseDTO)
            .toList();
    }

    public CoverLetterResponseDTO create(CoverLetterCreateDTO dto) {
        CoverLetter entity = mapper.toEntity(dto);
        CoverLetter saved = repo.save(entity);

        auditLogService.logCreate("Created new cover letter with id: " + saved.getId());

        return mapper.toResponseDTO(saved);
    }

    public CoverLetter getModelById(UUID id) {
        return repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cover letter not found"));
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted cover letter with id: " + id);
    }

}
