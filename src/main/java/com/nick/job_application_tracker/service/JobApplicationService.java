package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.JobApplicationResponseDTO;
import com.nick.job_application_tracker.mapper.JobApplicationMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.CoverLetterRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.JobSourceRepository;
import com.nick.job_application_tracker.repository.LocationRepository;
import com.nick.job_application_tracker.repository.ResumeRepository;

@Service
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final LocationRepository locationRepository;
    private final JobSourceRepository jobSourceRepository;
    private final ResumeRepository resumeRepository;
    private final CoverLetterRepository coverLetterRepository;
    private final AuditLogService auditLogService;

    public JobApplicationService(
        JobApplicationRepository jobApplicationRepository,
        LocationRepository locationRepository,
        JobSourceRepository jobSourceRepository,
        ResumeRepository resumeRepository,
        CoverLetterRepository coverLetterRepository,
        AuditLogService auditLogService // <- new!
    ) {
        this.jobApplicationRepository = jobApplicationRepository;
        this.locationRepository = locationRepository;
        this.jobSourceRepository = jobSourceRepository;
        this.resumeRepository = resumeRepository;
        this.coverLetterRepository = coverLetterRepository;
        this.auditLogService = auditLogService;
    }


    public JobApplicationResponseDTO create(JobApplicationCreateDTO dto) {
        JobApplication jobApp = JobApplicationMapper.toEntity(dto);

        // Set or create location
        Location location = locationRepository.findByCityAndCountry(dto.getLocationCity(), dto.getLocationCountry())
            .orElseGet(() -> {
                Location loc = new Location();
                loc.setCity(dto.getLocationCity());
                loc.setCountry(dto.getLocationCountry());
                return locationRepository.save(loc);
            });
        jobApp.setLocation(location);

        // Set source, resume, and cover letter if present
        if (dto.getSourceId() != null) {
            JobSource source = jobSourceRepository.findById(dto.getSourceId())
                    .orElseThrow(() -> new RuntimeException("Source not found"));
            jobApp.setSource(source);
        }

        if (dto.getResumeId() != null) {
            Resume resume = resumeRepository.findById(dto.getResumeId())
                    .orElseThrow(() -> new RuntimeException("Resume not found"));
            jobApp.setResume(resume);
        }

        if (dto.getCoverLetterId() != null) {
            CoverLetter coverLetter = coverLetterRepository.findById(dto.getCoverLetterId())
                    .orElseThrow(() -> new RuntimeException("Cover letter not found"));
            jobApp.setCoverLetter(coverLetter);
        }

        JobApplication saved = jobApplicationRepository.save(jobApp);
        auditLogService.logCreate("Created job application with id: " + saved.getId());
        return JobApplicationMapper.toResponseDTO(saved);
    }

    public List<JobApplicationResponseDTO> getAll() {
        return jobApplicationRepository.findAll().stream()
                .map(JobApplicationMapper::toResponseDTO)
                .collect((Collectors.toList()));
    }

    public JobApplicationDetailDTO getById(Long id) {
        JobApplication jobApp = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found"));
        return JobApplicationMapper.toDetailDTO(jobApp);
    }

    public JobApplication findById(Long id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found"));
    }

    public void delete(Long id) {
        JobApplication jobApp = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found"));
    
        jobApplicationRepository.delete(jobApp);
        auditLogService.logDelete("Deleted job application with id: " + id);
    }
    
}
