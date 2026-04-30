package com.nick.job_application_tracker.service.inter_face;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CoverLetterDTO;
import com.nick.job_application_tracker.mapper.CoverLetterMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.inter_face.CoverLetterRepository;

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
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        CoverLetter saved = repo.save(entity);

        if (isNew) {
            auditLogService.logCreate("Created new cover letter with id: " + saved.getId());
        } else {
            auditLogService.logUpdate("Updated cover letter with id: " + saved.getId());
        }

        return mapper.toDTO(saved);
    }

    public CoverLetter getModelById(UUID id) {
        return repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Cover letter not found"));
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted cover letter with id: " + id);
    }

    public void delete(Long id) {
        delete(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id));
    }
}
