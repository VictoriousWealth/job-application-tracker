package com.nick.job_application_tracker.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.AuditLogRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void log(AuditLog.Action action, String description) {
        User performedBy = userRepository.findAll().stream().findFirst().orElse(null);
        if (performedBy == null) {
            return;
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setDescription(description);
        auditLog.setPerformedBy(performedBy);
        auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLogDTO> findAll() {
        return auditLogRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    @Override
    public AuditLogDTO getById(UUID id) {
        return auditLogRepository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    @Override
    public AuditLogDTO save(AuditLogDTO dto) {
        if (dto == null || dto.getUserId() == null) {
            return null;
        }

        User user = userRepository.findById(dto.getUserId()).orElse(null);
        if (user == null) {
            return null;
        }

        AuditLog auditLog = new AuditLog();
        if (dto.getId() != null) {
            auditLog.setId(dto.getId());
        }
        auditLog.setAction(dto.getAction() == null ? null : AuditLog.Action.from(dto.getAction()));
        auditLog.setDescription(dto.getDescription());
        auditLog.setPerformedBy(user);

        AuditLog saved = auditLogRepository.save(auditLog);
        return toDto(saved);
    }

    private AuditLogDTO toDto(AuditLog auditLog) {
        return new AuditLogDTO(
            auditLog.getId(),
            auditLog.getAction() == null ? null : auditLog.getAction().name(),
            auditLog.getDescription(),
            LocalDateTime.now(),
            auditLog.getPerformedBy() == null ? null : auditLog.getPerformedBy().getId()
        );
    }
}
