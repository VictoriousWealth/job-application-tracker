package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.mapper.AuditLogMapper;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.repository.AuditLogRepository;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;
    private final AuditLogMapper mapper;

    public AuditLogService(AuditLogRepository repo, AuditLogMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    public List<AuditLogDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public AuditLogDTO save(AuditLogDTO dto) {
        AuditLog log = mapper.toEntity(dto);
        return mapper.toDTO(repo.save(log));
    }
}
