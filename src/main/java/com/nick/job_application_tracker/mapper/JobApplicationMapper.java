package com.nick.job_application_tracker.mapper;

import com.nick.job_application_tracker.dto.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.JobApplicationResponseDTO;
import com.nick.job_application_tracker.model.JobApplication;

public class JobApplicationMapper {

    public static JobApplication toEntity(JobApplicationCreateDTO dto) {
        JobApplication entity = new JobApplication();
        entity.setJobTitle(dto.getJobTitle());
        entity.setCompany(dto.getCompany());
        entity.setStatus(dto.getStatus());
        entity.setNotes(dto.getNotes());
        entity.setAppliedOn(dto.getAppliedOn());
        // Location, Resume, CoverLetter, Source set in service
        return entity;
    }

    public static JobApplicationResponseDTO toResponseDTO(JobApplication jobApp) {
        JobApplicationResponseDTO dto = new JobApplicationResponseDTO();
        dto.id = jobApp.getId();
        dto.jobTitle = jobApp.getJobTitle();
        dto.company = jobApp.getCompany();
        dto.status = jobApp.getStatus();
        dto.notes = jobApp.getNotes();
        dto.appliedOn = jobApp.getAppliedOn();
        dto.locationCity = jobApp.getLocation() != null ? jobApp.getLocation().getCity() : null;
        dto.locationCountry = jobApp.getLocation() != null ? jobApp.getLocation().getCountry() : null;
        dto.resumeName = jobApp.getResume() != null ? jobApp.getResume().getFilePath() : null;
        dto.coverLetterName = jobApp.getCoverLetter() != null ? jobApp.getCoverLetter().getFilePath() : null;
        dto.sourceName = jobApp.getSource() != null ? jobApp.getSource().getName() : null;
        return dto;
    }

    public static JobApplicationDetailDTO toDetailDTO(JobApplication jobApp) {
        JobApplicationDetailDTO dto = new JobApplicationDetailDTO();
        dto.id = jobApp.getId();
        dto.jobTitle = jobApp.getJobTitle();
        dto.company = jobApp.getCompany();
        dto.status = jobApp.getStatus();
        dto.notes = jobApp.getNotes();
        dto.appliedOn = jobApp.getAppliedOn();
        dto.locationCity = jobApp.getLocation() != null ? jobApp.getLocation().getCity() : null;
        dto.locationCountry = jobApp.getLocation() != null ? jobApp.getLocation().getCountry() : null;
        dto.resumeId = jobApp.getResume() != null ? jobApp.getResume().getId() : null;
        dto.coverLetterId = jobApp.getCoverLetter() != null ? jobApp.getCoverLetter().getId() : null;
        dto.sourceId = jobApp.getSource() != null ? jobApp.getSource().getId() : null;
        return dto;
    }
}
