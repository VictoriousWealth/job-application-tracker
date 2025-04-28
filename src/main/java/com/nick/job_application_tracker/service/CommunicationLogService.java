package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.mapper.CommunicationLogMapper;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.repository.CommunicationLogRepository;

@Service
public class CommunicationLogService {

    private final CommunicationLogRepository repo;
    private final CommunicationLogMapper mapper;
    private final AuditLogService auditLogService;

    public CommunicationLogService(CommunicationLogRepository repo, CommunicationLogMapper mapper, AuditLogService auditLogService) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<CommunicationLogDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(mapper::toDTO)
            .toList();
    }

    public CommunicationLogDTO save(CommunicationLogDTO dto) {
        CommunicationLog entity = mapper.toEntity(dto);
        CommunicationLog saved = repo.save(entity);

        if (dto.getId() == null) {
            auditLogService.logCreate("Created CommunicationLog for JobApplication ID " + dto.getJobApplicationId());
        } else {
            auditLogService.logUpdate("Updated CommunicationLog ID " + saved.getId() + " for JobApplication ID " + dto.getJobApplicationId());
        }

        return mapper.toDTO(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted CommunicationLog ID " + id);
    }
}
