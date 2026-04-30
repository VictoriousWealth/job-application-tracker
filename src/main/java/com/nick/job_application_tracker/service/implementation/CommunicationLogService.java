package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.CommunicationLogCreateDTO;
import com.nick.job_application_tracker.dto.response.CommunicationLogResponseDTO;
import com.nick.job_application_tracker.mapper.CommunicationLogMapper;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.repository.inter_face.CommunicationLogRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

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

    public List<CommunicationLogResponseDTO> getByJobAppId(UUID jobAppId) {
        return repo.findByJobApplicationIdAndDeletedFalse(jobAppId, Pageable.unpaged()).getContent().stream()
            .map(CommunicationLogMapper::toResponseDTO)
            .toList();
    }

    public CommunicationLogResponseDTO create(CommunicationLogCreateDTO dto) {
        var jobApplication = jobApplicationRepository.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new IllegalArgumentException("Job Application not found"));
        CommunicationLog entity = mapper.toEntity(dto, jobApplication);
        CommunicationLog saved = repo.save(entity);

        auditLogService.logCreate("Created CommunicationLog for JobApplication ID " + dto.getJobApplicationId());

        return CommunicationLogMapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted CommunicationLog ID " + id);
    }

}
