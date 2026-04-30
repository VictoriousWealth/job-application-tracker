package com.nick.job_application_tracker.service.inter_face;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.CommunicationLogDTO;
import com.nick.job_application_tracker.mapper.CommunicationLogMapper;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.repository.inter_face.CommunicationLogRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;

@Service
public class CommunicationLogService {

    private final CommunicationLogRepository repo;
    private final JobApplicationRepository jobApplicationRepository;
    private final CommunicationLogMapper mapper;
    private final AuditLogService auditLogService;

    public CommunicationLogService(
        CommunicationLogRepository repo,
        JobApplicationRepository jobApplicationRepository,
        CommunicationLogMapper mapper,
        AuditLogService auditLogService
    ) {
        this.repo = repo;
        this.jobApplicationRepository = jobApplicationRepository;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<CommunicationLogDTO> getByJobAppId(UUID jobAppId) {
        return repo.findByJobApplicationIdAndDeletedFalse(jobAppId, Pageable.unpaged()).getContent().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public List<CommunicationLogDTO> getByJobAppId(Long jobAppId) {
        return getByJobAppId(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(jobAppId));
    }

    public CommunicationLogDTO save(CommunicationLogDTO dto) {
        var jobApplication = jobApplicationRepository.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new IllegalArgumentException("Job Application not found"));
        CommunicationLog entity = mapper.toEntity(dto, jobApplication);
        CommunicationLog saved = repo.save(entity);

        if (dto.getId() == null) {
            auditLogService.logCreate("Created CommunicationLog for JobApplication ID " + dto.getJobApplicationId());
        } else {
            auditLogService.logUpdate("Updated CommunicationLog ID " + saved.getId() + " for JobApplication ID " + dto.getJobApplicationId());
        }

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted CommunicationLog ID " + id);
    }

    public void delete(Long id) {
        delete(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id));
    }
}
