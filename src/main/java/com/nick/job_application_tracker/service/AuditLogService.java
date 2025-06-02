package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.AuditLogDTO;
import com.nick.job_application_tracker.mapper.AuditLogMapper;
import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.AuditLogRepository;
import com.nick.job_application_tracker.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuditLogService {

    private final AuditLogRepository repo;
    private final AuditLogMapper mapper;
    private final UserRepository userRepository; 
    
    private void saveLog(AuditLog.Action action, String description) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setDescription(description);
        log.setCreatedAt(LocalDateTime.now());
        log.setPerformedBy(getCurrentUser()); 
        repo.save(log);
    }
    
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(username)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));
    }
    
    public AuditLogService(AuditLogRepository repo, AuditLogMapper mapper, UserRepository userRepository) {
        this.repo = repo;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    public List<AuditLogDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public AuditLogDTO save(AuditLogDTO dto) {
        User currentUser = getCurrentUser();
        AuditLog log = mapper.toEntity(dto, currentUser);
        return mapper.toDTO(repo.save(log));
    }



    public void logCreate(String description) {
        saveLog(AuditLog.Action.CREATE, description);
    }

    public void logUpdate(String description) {
        saveLog(AuditLog.Action.UPDATE, description);
    }

    public void logDelete(String description) {
        saveLog(AuditLog.Action.DELETE, description);
    }

}
