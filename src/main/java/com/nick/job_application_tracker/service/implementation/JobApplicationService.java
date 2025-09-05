package com.nick.job_application_tracker.service.implementation;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import com.nick.job_application_tracker.service.inter_face.ResumeService;
import com.nick.job_application_tracker.service.specialised_common.JobApplicationServiceInterface;
import com.nick.job_application_tracker.util.SecurityUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nick.job_application_tracker.dto.create.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.response.JobApplicationResponseDTO;
import com.nick.job_application_tracker.dto.update.JobApplicationUpdateDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.exception.client_exception.specialised_case.InvalidFieldException;
import com.nick.job_application_tracker.mapper.JobApplicationMapper;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.CoverLetterService;
import com.nick.job_application_tracker.service.inter_face.JobSourceService;


@Service
public class JobApplicationService implements JobApplicationServiceInterface{

    private final User user;
    
    private static final Supplier<NotFoundException> EXCEPTION_SUPPLIER = () -> new NotFoundException("Job application not found", null);

    @SuppressWarnings("unused")
    private final UserRepository userRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final JobSourceService jobSourceService;

    private final CoverLetterService coverLetterService;

    private final ResumeService resumeService;
    
    private final LocationService locationService;

    public JobApplicationService(LocationService locationService, ResumeService resumeService, CoverLetterService coverLetterService, JobSourceService jobSourceService, JobApplicationRepository jobApplicationRepository, UserRepository userRepository) {
        this.locationService = locationService;
        this.resumeService = resumeService;
        this.coverLetterService = coverLetterService;
        this.jobSourceService = jobSourceService;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.user = SecurityUtils.getCurrentUserOrThrow(userRepository);
    }

    @Override
    public JobApplicationResponseDTO create(JobApplicationCreateDTO dto) {
        JobSource jobSource = jobSourceService.getModelById(dto.getSourceId());
        Location location = null;
        Resume resume = null;
        CoverLetter coverLetter = null;

        if (dto.getLocationId() != null) {
            //  we need to check if the location exists
            // if not, throw an exception
            UUID locationId = dto.getLocationId();
            location = locationService.getModelById(locationId);
        } // if statement here bc u can dis-associate a location from this entity
        
        if (dto.getResumeId() != null) {
            //  we need to check if the resume exists
            // if not, throw an exception
            UUID resumeId = dto.getResumeId();
            resume = resumeService.getModelById(resumeId);
        } // if statement here bc u can dis-associate a resume from this entity

        if (dto.getCoverLetterId() == null) {
            //  we need to check if the company exists
            // if not, throw an exception
            UUID coverLetterId = dto.getCoverLetterId();
            coverLetter = coverLetterService.getModelById(coverLetterId);
        } // if statement here bc u can dis-associate a cover letter from this entity


        // now all the id checks are done, we can create the job application entity
        JobApplication jobApplication = JobApplicationMapper.toEntity(dto, location, jobSource, resume, coverLetter);
        jobApplication.setUser(user);

        JobApplication saved = jobApplicationRepository.save(jobApplication);
        return JobApplicationMapper.toResponseDTO(saved);
    
    }

    @Override
    public JobApplicationDetailDTO getDetailById(UUID id) {
        return JobApplicationMapper.toDetailDTO(getModelById(id));
    }

    @Override
    public JobApplication getModelById(UUID id) {
        return jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(EXCEPTION_SUPPLIER);
    }

    @Override
    public Page<JobApplicationResponseDTO> getAll(Pageable pageable) {
        Page<JobApplication> jobApplications = jobApplicationRepository.findByUserIdAndDeletedFalse(user.getId(), pageable);
        return jobApplications.map(JobApplicationMapper::toResponseDTO);
    }

    @Override
    public JobApplicationDetailDTO patchById(UUID id, JsonNode node) {
        JobApplication jobApplication = jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(EXCEPTION_SUPPLIER);
        
        
        // nend to deserialse node
        for (String field : JobApplicationMapper.patchableFields) {
            if (!node.has(field)) continue;

            JsonNode value = node.get(field);
            switch (field) {
                case "jobTitle" -> {
                    if(value == null) throw new InvalidFieldException("jobTitle", null);
                    jobApplication.setJobTitle(value.asText());
                }
                case "company" -> {
                    if(value == null) throw new InvalidFieldException("company", null);
                    jobApplication.setCompany(value.asText());
                }
                case "status" -> jobApplication.setStatus(value == null ? null : Status.valueOf(value.asText()));
                case "notes" -> jobApplication.setNotes(value == null ? null : value.asText(null));
                case "appliedOn" -> jobApplication.setAppliedOn(value == null ? null : LocalDateTime.parse(value.asText()));
                case "deadline" -> jobApplication.setDeadline(value == null ? null : LocalDateTime.parse(value.asText()));
                case "jobDescription" -> {
                    if(value == null) throw new InvalidFieldException("jobDescription", null);
                    jobApplication.setJobDescription(value.asText());
                }

                case "locationId" -> {
                    UUID locationId = value == null ? null : UUID.fromString(value.asText()) ;
                    jobApplication.setLocation(locationId == null ? null : locationService.getModelById(locationId));
                }

                case "sourceId" -> {
                    if (value == null) throw new InvalidFieldException("sourceId", null);
                    UUID sourceId = UUID.fromString(value.asText());
                    jobApplication.setSource(jobSourceService.getModelById(sourceId));
                }

                case "resumeId" -> {
                    UUID resumeId = value == null ? null : UUID.fromString(value.asText());
                    jobApplication.setResume(resumeId == null ? null : resumeService.getModelById(resumeId));
                }

                case "coverLetterId" -> {
                    UUID coverLetterId = value == null ? null : UUID.fromString(value.asText());
                    jobApplication.setCoverLetter(coverLetterId == null ? null : coverLetterService.getModelById(coverLetterId));
                }

                default -> {
                    // We could throw an exception here if we want to be strict
                }
            }
        }

        JobApplication saved = jobApplicationRepository.save(jobApplication);
        return JobApplicationMapper.toDetailDTO(saved);
    }



    @Override
    public JobApplicationDetailDTO updateById(UUID id, JobApplicationUpdateDTO dto) {
        // Fetch the existing entity
        JobApplication jobApplication = jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(EXCEPTION_SUPPLIER);

        // Optional related entities (validated only if present)
        Location location = null;
        if (dto.getLocationId() != null) {
            location = locationService.getModelById(dto.getLocationId());
        }

        Resume resume = null;
        if (dto.getResumeId() != null) {
            resume = resumeService.getModelById(dto.getResumeId());
        }

        CoverLetter coverLetter = null;
        if (dto.getCoverLetterId() != null) {
            coverLetter = coverLetterService.getModelById(dto.getCoverLetterId());
        }

        // Update fields
        JobSource jobSource = jobSourceService.getModelById(dto.getSourceId());
        JobApplication updated = JobApplicationMapper.updateEntityWithDTOInfo(jobApplication, dto, location, jobSource, resume, coverLetter);

        // Save and return updated entity
        JobApplication saved = jobApplicationRepository.save(updated);
        return JobApplicationMapper.toDetailDTO(saved);
    }

    @Override
    public String deleteById(UUID id) {
        // Fetch the existing entity
        JobApplication jobApplication = jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(EXCEPTION_SUPPLIER);
        jobApplication.setDeleted(true);
        jobApplicationRepository.save(jobApplication);
        return "No Content";
    }

}
