package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.ResumeCreateDTO;
import com.nick.job_application_tracker.dto.response.ResumeResponseDTO;
import com.nick.job_application_tracker.mapper.ResumeMapper;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.inter_face.ResumeRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuditLogService auditLogService;

    public ResumeService(ResumeRepository resumeRepository, AuditLogService auditLogService) {
        this.resumeRepository = resumeRepository;
        this.auditLogService = auditLogService;
    }

    public List<ResumeResponseDTO> findAll() {
        return resumeRepository.findAll()
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
        return resumeRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Resume not found"));
    }

    public void delete(UUID id) {
        resumeRepository.deleteById(id);
        auditLogService.logDelete("Deleted Resume with ID " + id);
    }

}
