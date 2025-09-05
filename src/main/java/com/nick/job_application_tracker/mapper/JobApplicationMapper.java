package com.nick.job_application_tracker.mapper;


import java.util.Set;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.create.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.response.JobApplicationResponseDTO;
import com.nick.job_application_tracker.dto.update.JobApplicationUpdateDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.Resume;

@Component
public class JobApplicationMapper {

    public static final Set<String> patchableFields = Set.of(
        "jobTitle",
        "company",
        "status",
        "notes",
        "appliedOn",
        "deadline",
        "jobDescription",
        "locationId",
        "resumeId",
        "coverLetterId",
        "sourceId"
    );


    public static JobApplication updateEntityWithDTOInfo(JobApplication jobApplication, JobApplicationUpdateDTO dto, Location location, JobSource jobSource, Resume resume, CoverLetter coverLetter) {
        jobApplication.setJobTitle(dto.getJobTitle());
        jobApplication.setCompany(dto.getCompany());
        jobApplication.setStatus(dto.getStatus());
        jobApplication.setNotes(dto.getNotes());
        jobApplication.setAppliedOn(dto.getAppliedOn());
        jobApplication.setDeadline(dto.getDeadline());
        jobApplication.setJobDescription(dto.getJobDescription());
        jobApplication.setLocation(location);
        jobApplication.setSource(jobSource);
        jobApplication.setResume(resume);
        jobApplication.setCoverLetter(coverLetter);   
        return jobApplication;
    }

    public static JobApplication toEntity(
        JobApplicationCreateDTO dto, 
        Location location, 
        JobSource jobSource, 
        Resume resume, 
        CoverLetter coverLetter
        )
        {
            JobApplication entity = new JobApplication();
            entity.setJobTitle(dto.getJobTitle());
            entity.setCompany(dto.getCompany());
            entity.setStatus(dto.getStatus());
            entity.setNotes(dto.getNotes());
            entity.setAppliedOn(dto.getAppliedOn());
            entity.setDeadline(dto.getDeadline());
            entity.setJobDescription(dto.getJobDescription());
            entity.setLocation(location);
            entity.setSource(jobSource);
            entity.setResume(resume);
            entity.setCoverLetter(coverLetter);
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
        dto.deadline = jobApp.getDeadline();
        dto.jobDescription = jobApp.getJobDescription();
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
        dto.deadline = jobApp.getDeadline();
        dto.jobDescription = jobApp.getJobDescription();
        dto.locationId = jobApp.getLocation() != null ? jobApp.getLocation().getId() : null;
        dto.resumeId = jobApp.getResume() != null ? jobApp.getResume().getId() : null;
        dto.coverLetterId = jobApp.getCoverLetter() != null ? jobApp.getCoverLetter().getId() : null;
        dto.sourceId = jobApp.getSource() != null ? jobApp.getSource().getId() : null;
        return dto;
    }

   
}
