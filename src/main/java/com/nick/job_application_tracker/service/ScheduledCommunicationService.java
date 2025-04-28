package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.ScheduledCommunicationDTO;
import com.nick.job_application_tracker.mapper.ScheduledCommunicationMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.ScheduledCommunicationRepository;

@Service
public class ScheduledCommunicationService {

    private final ScheduledCommunicationRepository repository;
    private final JobApplicationRepository jobAppRepository;
    private final AuditLogService auditLogService;

    public ScheduledCommunicationService(ScheduledCommunicationRepository repository,
                                          JobApplicationRepository jobAppRepository,
                                          AuditLogService auditLogService) {
        this.repository = repository;
        this.jobAppRepository = jobAppRepository;
        this.auditLogService = auditLogService;
    }

    public List<ScheduledCommunicationDTO> getAll() {
        return repository.findAll().stream()
            .map(ScheduledCommunicationMapper::toDTO)
            .collect(Collectors.toList());
    }

    public ScheduledCommunicationDTO getById(Long id) {
        return repository.findById(id)
            .map(ScheduledCommunicationMapper::toDTO)
            .orElseThrow(() -> new RuntimeException("ScheduledCommunication not found"));
    }

    public ScheduledCommunicationDTO create(ScheduledCommunicationCreateDTO dto) {
        JobApplication jobApp = jobAppRepository.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new RuntimeException("Job Application not found"));
        ScheduledCommunication entity = ScheduledCommunicationMapper.toEntity(dto, jobApp);
        ScheduledCommunication savedEntity = repository.save(entity);
        
        auditLogService.logCreate("Created ScheduledCommunication with ID " + savedEntity.getId() + " for JobApplication ID " + jobApp.getId());
        
        return ScheduledCommunicationMapper.toDTO(savedEntity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
        auditLogService.logDelete("Deleted ScheduledCommunication with ID " + id);
    }
}
