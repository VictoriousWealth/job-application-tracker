package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.CoverLetterCreateDTO;
import com.nick.job_application_tracker.exception.client.NotFoundException;
import com.nick.job_application_tracker.dto.response.CoverLetterResponseDTO;
import com.nick.job_application_tracker.mapper.CoverLetterMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.interfaces.CoverLetterRepository;
import com.nick.job_application_tracker.repository.interfaces.UserRepository;
import com.nick.job_application_tracker.service.interfaces.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class CoverLetterService {

    private final CoverLetterRepository repo;
    private final CoverLetterMapper mapper;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Autowired
    public CoverLetterService(CoverLetterRepository repo, CoverLetterMapper mapper, AuditLogService auditLogService, UserRepository userRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public CoverLetterService(CoverLetterRepository repo, CoverLetterMapper mapper, AuditLogService auditLogService) {
        this(repo, mapper, auditLogService, null);
    }

    public List<CoverLetterResponseDTO> findAll() {
        return findVisibleCoverLetters().stream()
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
        String currentUserEmail = getCurrentUserEmailOrNull();
        if (currentUserEmail == null) {
            return repo.findById(id)
                .filter(coverLetter -> !coverLetter.isDeleted())
                .orElseThrow(() -> new NotFoundException("Cover letter not found", null));
        }

        return repo.findByIdAndCreatedByAndDeletedFalse(id, currentUserEmail)
            .orElseThrow(() -> new NotFoundException("Cover letter not found", null));
    }

    public void delete(UUID id) {
        CoverLetter coverLetter = getModelById(id);
        coverLetter.softDelete();
        repo.save(coverLetter);
        auditLogService.logDelete("Deleted cover letter with id: " + id);
    }

    private List<CoverLetter> findVisibleCoverLetters() {
        String currentUserEmail = getCurrentUserEmailOrNull();
        if (currentUserEmail == null) {
            return repo.findAll().stream()
                .filter(coverLetter -> !coverLetter.isDeleted())
                .toList();
        }
        return repo.findByCreatedByAndDeletedFalse(currentUserEmail, Pageable.unpaged()).getContent();
    }

    private String getCurrentUserEmailOrNull() {
        if (userRepository == null) {
            return null;
        }
        try {
            return SecurityUtils.getCurrentUserOrThrow(userRepository).getEmail();
        } catch (IllegalStateException ex) {
            return null;
        }
    }
}
