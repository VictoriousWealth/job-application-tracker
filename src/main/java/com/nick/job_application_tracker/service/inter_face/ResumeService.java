package com.nick.job_application_tracker.service.inter_face;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.mapper.ResumeMapper;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.inter_face.ResumeRepository;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuditLogService auditLogService;

    public ResumeService(ResumeRepository resumeRepository, AuditLogService auditLogService) {
        this.resumeRepository = resumeRepository;
        this.auditLogService = auditLogService;
    }

    public List<ResumeDTO> findAll() {
        return resumeRepository.findAll()
            .stream()
            .map(ResumeMapper::toDTO)
            .collect(Collectors.toList());
    }

    public ResumeDTO save(ResumeDTO dto) {
        Resume resume = ResumeMapper.toEntity(dto);
        if (dto.getId() != null) {
            resume.setId(dto.getId());
        }
        Resume saved = resumeRepository.save(resume);
        
        auditLogService.logCreate("Created Resume with ID " + saved.getId());
        
        return ResumeMapper.toDTO(saved);
    }

    public Resume getModelById(UUID id) {
        return resumeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
    }

    public void delete(UUID id) {
        resumeRepository.deleteById(id);
        auditLogService.logDelete("Deleted Resume with ID " + id);
    }

    public void delete(Long id) {
        delete(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id));
    }
}
