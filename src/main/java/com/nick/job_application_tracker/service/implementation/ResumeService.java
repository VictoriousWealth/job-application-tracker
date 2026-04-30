package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.ResumeCreateDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.dto.response.ResumeResponseDTO;
import com.nick.job_application_tracker.mapper.ResumeMapper;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.inter_face.ResumeRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Autowired
    public ResumeService(ResumeRepository resumeRepository, AuditLogService auditLogService, UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public ResumeService(ResumeRepository resumeRepository, AuditLogService auditLogService) {
        this(resumeRepository, auditLogService, null);
    }

    public List<ResumeResponseDTO> findAll() {
        return findVisibleResumes()
            .stream()
            .map(ResumeMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    public ResumeResponseDTO create(ResumeCreateDTO dto) {
        Resume resume = ResumeMapper.toEntity(dto);
        Resume saved = resumeRepository.save(resume);
        
        auditLogService.logCreate("Created Resume with ID " + saved.getId());
        
        return ResumeMapper.toResponseDTO(saved);
    }

    public Resume getModelById(UUID id) {
        String currentUserEmail = getCurrentUserEmailOrNull();
        if (currentUserEmail == null) {
            return resumeRepository.findById(id)
                .filter(resume -> !resume.isDeleted())
                .orElseThrow(() -> new NotFoundException("Resume not found", null));
        }

        return resumeRepository.findByIdAndCreatedByAndDeletedFalse(id, currentUserEmail)
            .orElseThrow(() -> new NotFoundException("Resume not found", null));
    }

    public void delete(UUID id) {
        Resume resume = getModelById(id);
        resume.softDelete();
        resumeRepository.save(resume);
        auditLogService.logDelete("Deleted Resume with ID " + id);
    }

    private List<Resume> findVisibleResumes() {
        String currentUserEmail = getCurrentUserEmailOrNull();
        if (currentUserEmail == null) {
            return resumeRepository.findAllByDeletedFalse(Pageable.unpaged()).getContent();
        }
        return resumeRepository.findByCreatedByAndDeletedFalse(currentUserEmail, Pageable.unpaged()).getContent();
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
