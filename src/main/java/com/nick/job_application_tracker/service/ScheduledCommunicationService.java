package com.nick.job_application_tracker.service;

import java.util.List;

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

    private final ScheduledCommunicationRepository repo;
    private final JobApplicationRepository jobApplicationRepo;
    private final AuditLogService auditLogService;

    public ScheduledCommunicationService(
        ScheduledCommunicationRepository repo,
        JobApplicationRepository jobApplicationRepo,
        AuditLogService auditLogService
    ) {
        this.repo = repo;
        this.jobApplicationRepo = jobApplicationRepo;
        this.auditLogService = auditLogService;
    }

    public List<ScheduledCommunicationDTO> getByJobAppId(Long jobAppId) {
        return repo.findByJobApplicationId(jobAppId).stream()
            .map(ScheduledCommunicationMapper::toDTO)
            .toList();
    }

    public ScheduledCommunicationDTO save(ScheduledCommunicationCreateDTO dto) {
        JobApplication application = jobApplicationRepo.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new RuntimeException("Job Application not found"));

        ScheduledCommunication comm = ScheduledCommunicationMapper.toEntity(dto, application);
        ScheduledCommunication saved = repo.save(comm);

        auditLogService.logCreate("Created scheduled communication with id: " + saved.getId());

        return ScheduledCommunicationMapper.toDTO(saved);
    }

    public void delete(Long id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted scheduled communication with id: " + id);
    }
}
