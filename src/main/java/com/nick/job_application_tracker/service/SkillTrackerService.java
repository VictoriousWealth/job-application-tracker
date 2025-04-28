package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.SkillTrackerDTO;
import com.nick.job_application_tracker.mapper.SkillTrackerMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.SkillTrackerRepository;

@Service
public class SkillTrackerService {

    private final SkillTrackerRepository skillRepo;
    private final JobApplicationRepository jobAppRepo;
    private final AuditLogService auditLogService;

    public SkillTrackerService(
        SkillTrackerRepository skillRepo,
        JobApplicationRepository jobAppRepo,
        AuditLogService auditLogService
    ) {
        this.skillRepo = skillRepo;
        this.jobAppRepo = jobAppRepo;
        this.auditLogService = auditLogService;
    }

    public SkillTrackerDTO create(SkillTrackerCreateDTO dto) {
        JobApplication jobApp = jobAppRepo.findById(dto.getJobApplicationId())
            .orElseThrow(() -> new RuntimeException("Job application not found"));

        SkillTracker skill = SkillTrackerMapper.toEntity(dto, jobApp);
        SkillTracker saved = skillRepo.save(skill);

        auditLogService.logCreate("Created skill '" + saved.getSkillName() + "' for job application ID: " + jobApp.getId());

        return SkillTrackerMapper.toDTO(saved);
    }

    public List<SkillTrackerDTO> getByJobAppId(Long jobAppId) {
        return skillRepo.findByJobApplicationId(jobAppId).stream()
            .map(SkillTrackerMapper::toDTO)
            .toList();
    }

    public void delete(Long id) {
        skillRepo.deleteById(id);
        auditLogService.logDelete("Deleted skill tracker entry with ID: " + id);
    }
}
