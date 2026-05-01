package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.CommunicationLogCreateDTO;
import com.nick.job_application_tracker.dto.response.CommunicationLogResponseDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.CommunicationLogMapper;
import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.interfaces.CommunicationLogRepository;
import com.nick.job_application_tracker.repository.interfaces.JobApplicationRepository;
import com.nick.job_application_tracker.repository.interfaces.UserRepository;
import com.nick.job_application_tracker.service.interfaces.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class CommunicationLogService {

    private final CommunicationLogRepository repo;
    private final JobApplicationRepository jobApplicationRepository;
    private final CommunicationLogMapper mapper;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Autowired
    public CommunicationLogService(
        CommunicationLogRepository repo,
        JobApplicationRepository jobApplicationRepository,
        CommunicationLogMapper mapper,
        AuditLogService auditLogService,
        UserRepository userRepository
    ) {
        this.repo = repo;
        this.jobApplicationRepository = jobApplicationRepository;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public CommunicationLogService(
        CommunicationLogRepository repo,
        JobApplicationRepository jobApplicationRepository,
        CommunicationLogMapper mapper,
        AuditLogService auditLogService
    ) {
        this(repo, jobApplicationRepository, mapper, auditLogService, null);
    }

    public List<CommunicationLogResponseDTO> getByJobAppId(UUID jobAppId) {
        JobApplication jobApplication = getAccessibleJobApplication(jobAppId);
        return repo.findByJobApplicationIdAndDeletedFalse(jobApplication.getId(), Pageable.unpaged()).getContent().stream()
            .map(CommunicationLogMapper::toResponseDTO)
            .toList();
    }

    public CommunicationLogResponseDTO create(CommunicationLogCreateDTO dto) {
        JobApplication jobApplication = getAccessibleJobApplication(dto.getJobApplicationId());
        CommunicationLog entity = mapper.toEntity(dto, jobApplication);
        CommunicationLog saved = repo.save(entity);

        auditLogService.logCreate("Created CommunicationLog for JobApplication ID " + dto.getJobApplicationId());

        return CommunicationLogMapper.toResponseDTO(saved);
    }

    public void delete(UUID id) {
        CommunicationLog communicationLog = getAccessibleCommunicationLog(id);
        communicationLog.softDelete();
        repo.save(communicationLog);
        auditLogService.logDelete("Deleted CommunicationLog ID " + id);
    }

    private JobApplication getAccessibleJobApplication(UUID jobApplicationId) {
        if (userRepository == null) {
            return jobApplicationRepository.findById(jobApplicationId)
                .filter(jobApplication -> !jobApplication.isDeleted())
                .orElseThrow(() -> new NotFoundException("Job application not found", null));
        }

        User user = SecurityUtils.getCurrentUserOrThrow(userRepository);
        return jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(jobApplicationId, user.getId())
            .orElseThrow(() -> new NotFoundException("Job application not found", null));
    }

    private CommunicationLog getAccessibleCommunicationLog(UUID id) {
        if (userRepository == null) {
            return repo.findById(id)
                .filter(log -> !log.isDeleted())
                .orElseThrow(() -> new NotFoundException("Communication log not found", null));
        }

        User user = SecurityUtils.getCurrentUserOrThrow(userRepository);
        return repo.findByIdAndJobApplicationUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(() -> new NotFoundException("Communication log not found", null));
    }
}
