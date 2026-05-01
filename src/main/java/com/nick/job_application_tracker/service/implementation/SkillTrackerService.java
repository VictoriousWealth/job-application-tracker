package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.create.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.response.SkillTrackerResponseDTO;
import com.nick.job_application_tracker.mapper.SkillTrackerMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.interfaces.SkillTrackerRepository;
import com.nick.job_application_tracker.service.interfaces.AuditLogService;

@Service
public class SkillTrackerService {

    private final SkillTrackerRepository repo;
    private final JobApplicationService jobApplicationService;
    private final AuditLogService auditLogService;

    public SkillTrackerService(
        SkillTrackerRepository repo,
        JobApplicationService jobApplicationService,
        AuditLogService auditLogService
    ) {
        this.repo = repo;
        this.jobApplicationService = jobApplicationService;
        this.auditLogService = auditLogService;
    }

    public List<SkillTrackerResponseDTO> getByJobAppId(UUID jobAppId) {
        return repo.findByJobApplicationIdAndDeletedFalse(jobAppId, Pageable.unpaged()).getContent().stream()
            .map(SkillTrackerMapper::toResponseDTO)
            .toList();
    }

    public SkillTrackerResponseDTO create(SkillTrackerCreateDTO dto) {
        JobApplication jobApplication = jobApplicationService.getModelById(dto.getJobApplicationId());
        SkillTracker skill = SkillTrackerMapper.toEntity(dto, jobApplication);
        SkillTracker savedSkill = repo.save(skill);
        auditLogService.logCreate("Created SkillTracker with ID " + savedSkill.getId() + " for JobApplication ID " + savedSkill.getJobApplication().getId());
        return SkillTrackerMapper.toResponseDTO(savedSkill);
    }

    public void delete(UUID id) {
        repo.deleteById(id);
        auditLogService.logDelete("Deleted SkillTracker with ID " + id);
    }

}
