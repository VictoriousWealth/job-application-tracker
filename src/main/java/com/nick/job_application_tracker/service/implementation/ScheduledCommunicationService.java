package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.ScheduledCommunicationDetailDTO;
import com.nick.job_application_tracker.dto.response.ScheduledCommunicationResponseDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.ScheduledCommunicationMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.ScheduledCommunicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class ScheduledCommunicationService {

    private final ScheduledCommunicationRepository repository;
    private final JobApplicationRepository jobAppRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Autowired
    public ScheduledCommunicationService(
        ScheduledCommunicationRepository repository,
        JobApplicationRepository jobAppRepository,
        AuditLogService auditLogService,
        UserRepository userRepository
    ) {
        this.repository = repository;
        this.jobAppRepository = jobAppRepository;
        this.auditLogService = auditLogService;
        this.userRepository = userRepository;
    }

    public ScheduledCommunicationService(
        ScheduledCommunicationRepository repository,
        JobApplicationRepository jobAppRepository,
        AuditLogService auditLogService
    ) {
        this(repository, jobAppRepository, auditLogService, null);
    }

    public List<ScheduledCommunicationResponseDTO> getAll() {
        List<ScheduledCommunication> communications = userRepository == null
            ? repository.findAllByDeletedFalse(Pageable.unpaged()).getContent()
            : repository.findByJobApplicationUserIdAndDeletedFalse(getCurrentUser().getId(), Pageable.unpaged()).getContent();

        return communications.stream()
            .map(ScheduledCommunicationMapper::toResponseDTO)
            .collect(Collectors.toList());
    }

    public ScheduledCommunicationDetailDTO getById(UUID id) {
        return ScheduledCommunicationMapper.toDetailDTO(getAccessibleCommunication(id));
    }

    public ScheduledCommunicationResponseDTO create(ScheduledCommunicationCreateDTO dto) {
        JobApplication jobApp = getAccessibleJobApplication(dto.getJobApplicationId());
        ScheduledCommunication entity = ScheduledCommunicationMapper.toEntity(dto, jobApp);
        ScheduledCommunication savedEntity = repository.save(entity);

        auditLogService.logCreate("Created ScheduledCommunication with ID " + savedEntity.getId() + " for JobApplication ID " + jobApp.getId());

        return ScheduledCommunicationMapper.toResponseDTO(savedEntity);
    }

    public void delete(UUID id) {
        ScheduledCommunication communication = getAccessibleCommunication(id);
        communication.softDelete();
        repository.save(communication);
        auditLogService.logDelete("Deleted ScheduledCommunication with ID " + id);
    }

    private JobApplication getAccessibleJobApplication(UUID jobApplicationId) {
        if (userRepository == null) {
            return jobAppRepository.findById(jobApplicationId)
                .filter(jobApplication -> !jobApplication.isDeleted())
                .orElseThrow(() -> new NotFoundException("Job application not found", null));
        }

        User user = getCurrentUser();
        return jobAppRepository.findByIdAndUserIdAndDeletedFalse(jobApplicationId, user.getId())
            .orElseThrow(() -> new NotFoundException("Job application not found", null));
    }

    private ScheduledCommunication getAccessibleCommunication(UUID id) {
        if (userRepository == null) {
            return repository.findById(id)
                .filter(communication -> !communication.isDeleted())
                .orElseThrow(() -> new NotFoundException("Scheduled communication not found", null));
        }

        User user = getCurrentUser();
        return repository.findByIdAndJobApplicationUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(() -> new NotFoundException("Scheduled communication not found", null));
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUserOrThrow(userRepository);
    }
}
