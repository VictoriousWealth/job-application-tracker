package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.JobApplicationResponseDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.JobApplicationRepository;

@Service
public class JobApplicationService {
    private final JobApplicationRepository jobApplicationRepository;

    public JobApplicationService(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public JobApplicationResponseDTO create(JobApplicationCreateDTO dto) {
        JobApplication jobApp = new JobApplication();
        jobApp.setJobTitle(dto.jobTitle);
        jobApp.setCompany(dto.company);
        jobApp.setStatus(dto.status);
        jobApp.setNotes(dto.notes);
        jobApp.setAppliedOn(dto.appliedOn);
        // handle resumeId, coverLetterId, etc. here if needed

        JobApplication saved = jobApplicationRepository.save(jobApp);
        return toResponseDTO(saved);
    }

    public List<JobApplicationResponseDTO> getAll() {
        return jobApplicationRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public JobApplicationDetailDTO getById(Long id) {
        JobApplication jobApp = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job Application not found"));
        return toDetailDTO(jobApp);
    }

    // === Mapping Methods ===

    private JobApplicationResponseDTO toResponseDTO(JobApplication app) {
        JobApplicationResponseDTO dto = new JobApplicationResponseDTO();
        dto.id = app.getId();
        dto.jobTitle = app.getJobTitle();
        dto.company = app.getCompany();
        dto.status = app.getStatus();
        dto.notes = app.getNotes();
        dto.appliedOn = app.getAppliedOn();
        dto.locationCity = app.getLocation() != null ? app.getLocation().getCity() : null;
        dto.locationCountry = app.getLocation() != null ? app.getLocation().getCountry() : null;
        dto.sourceName = app.getSource() != null ? app.getSource().getName() : null;
        dto.resumeName = app.getResume() != null ? app.getResume().getFilePath() : null;
        dto.coverLetterName = app.getCoverLetter() != null ? app.getCoverLetter().getFilePath() : null;
        return dto;
    }

    private JobApplicationDetailDTO toDetailDTO(JobApplication app) {
        JobApplicationDetailDTO dto = new JobApplicationDetailDTO();
        dto.id = app.getId();
        dto.jobTitle = app.getJobTitle();
        dto.company = app.getCompany();
        dto.status = app.getStatus();
        dto.notes = app.getNotes();
        dto.appliedOn = app.getAppliedOn();
        dto.locationCity = app.getLocation() != null ? app.getLocation().getCity() : null;
        dto.locationCountry = app.getLocation() != null ? app.getLocation().getCountry() : null;
        dto.sourceId = app.getSource() != null ? app.getSource().getId() : null;
        dto.resumeId = app.getResume() != null ? app.getResume().getId() : null;
        dto.coverLetterId = app.getCoverLetter() != null ? app.getCoverLetter().getId() : null;
        return dto;
    }
}
