package com.nick.job_application_tracker.service.implementation;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.inter_face.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.service.specialised_common.JobApplicationServiceInterface;
import com.nick.job_application_tracker.util.SecurityUtils;

@Service
public class JobApplicationService implements JobApplicationServiceInterface {
    private static final Supplier<NotFoundException> EXCEPTION_SUPPLIER = () -> new NotFoundException("Job application not found", null);

    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobSourceService jobSourceService;
    private final CoverLetterService coverLetterService;
    private final ResumeService resumeService;
    private final LocationService locationService;
    private final AuditLogService auditLogService;
    private final ApplicationTimelineRepository applicationTimelineRepository;

    @Autowired
    public JobApplicationService(
        LocationService locationService,
        ResumeService resumeService,
        CoverLetterService coverLetterService,
        JobSourceService jobSourceService,
        JobApplicationRepository jobApplicationRepository,
        UserRepository userRepository,
        AuditLogService auditLogService,
        ApplicationTimelineRepository applicationTimelineRepository
    ) {
        this.locationService = locationService;
        this.resumeService = resumeService;
        this.coverLetterService = coverLetterService;
        this.jobSourceService = jobSourceService;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.auditLogService = auditLogService;
        this.applicationTimelineRepository = applicationTimelineRepository;
    }

    public JobApplicationService(
        LocationService locationService,
        ResumeService resumeService,
        CoverLetterService coverLetterService,
        JobSourceService jobSourceService,
        JobApplicationRepository jobApplicationRepository,
        UserRepository userRepository
    ) {
        this(locationService, resumeService, coverLetterService, jobSourceService, jobApplicationRepository, userRepository, null, null);
    }

    @Override
    public JobApplicationResponseDTO create(JobApplicationCreateDTO dto) {
        User user = getCurrentUser();
        JobSource jobSource = jobSourceService.getModelById(dto.getSourceId());
        Location location = resolveLocation(dto);
        Resume resume = dto.getResumeId() == null ? null : resumeService.getModelById(dto.getResumeId());
        CoverLetter coverLetter = dto.getCoverLetterId() == null ? null : coverLetterService.getModelById(dto.getCoverLetterId());

        JobApplication jobApplication = JobApplicationMapper.toEntity(dto, location, jobSource, resume, coverLetter);
        jobApplication.setUser(user);

        JobApplication saved = jobApplicationRepository.save(jobApplication);
        logCreate(saved);
        createTimelineEntry(
            saved,
            ApplicationTimeline.EventType.APPLICATION_CREATED,
            "Application created for " + saved.getCompany() + " - " + saved.getJobTitle()
        );
        return JobApplicationMapper.toResponseDTO(saved);
    }

    @Override
    public JobApplicationDetailDTO getDetailById(UUID id) {
        return JobApplicationMapper.toDetailDTO(getModelById(id));
    }

    @Override
    public JobApplication getModelById(UUID id) {
        User user = getCurrentUser();
        return jobApplicationRepository.findByIdAndUserIdAndDeletedFalse(id, user.getId())
            .orElseThrow(EXCEPTION_SUPPLIER);
    }

    @Override
    public Page<JobApplicationResponseDTO> getAll(Pageable pageable) {
        User user = getCurrentUser();
        Page<JobApplication> jobApplications = jobApplicationRepository.findByUserIdAndDeletedFalse(user.getId(), pageable);
        return jobApplications.map(JobApplicationMapper::toResponseDTO);
    }

    @Override
    public JobApplicationDetailDTO patchById(UUID id, JsonNode node) {
        JobApplication jobApplication = getModelById(id);
        Status previousStatus = jobApplication.getStatus();

        for (String field : JobApplicationMapper.patchableFields) {
            if (!node.has(field)) {
                continue;
            }

            JsonNode value = node.get(field);
            switch (field) {
                case "jobTitle" -> {
                    if (value == null) {
                        throw new InvalidFieldException("jobTitle", null);
                    }
                    jobApplication.setJobTitle(value.asText());
                }
                case "company" -> {
                    if (value == null) {
                        throw new InvalidFieldException("company", null);
                    }
                    jobApplication.setCompany(value.asText());
                }
                case "status" -> jobApplication.setStatus(value == null ? null : Status.from(value.asText()));
                case "notes" -> jobApplication.setNotes(value == null ? null : value.asText(null));
                case "appliedOn" -> jobApplication.setAppliedOn(value == null ? null : LocalDateTime.parse(value.asText()));
                case "deadline" -> jobApplication.setDeadline(value == null ? null : LocalDateTime.parse(value.asText()));
                case "jobDescription" -> {
                    if (value == null) {
                        throw new InvalidFieldException("jobDescription", null);
                    }
                    jobApplication.setJobDescription(value.asText());
                }
                case "locationId" -> {
                    UUID locationId = value == null ? null : UUID.fromString(value.asText());
                    jobApplication.setLocation(locationId == null ? null : locationService.getModelById(locationId));
                }
                case "sourceId" -> {
                    if (value == null) {
                        throw new InvalidFieldException("sourceId", null);
                    }
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
                }
            }
        }

        JobApplication saved = jobApplicationRepository.save(jobApplication);
        logUpdate(saved);
        createTimelineEntry(
            saved,
            resolveUpdateEventType(previousStatus, saved.getStatus()),
            buildUpdateDescription(saved, previousStatus, saved.getStatus())
        );
        return JobApplicationMapper.toDetailDTO(saved);
    }

    @Override
    public JobApplicationDetailDTO updateById(UUID id, JobApplicationUpdateDTO dto) {
        JobApplication jobApplication = getModelById(id);
        Status previousStatus = jobApplication.getStatus();

        Location location = dto.getLocationId() == null ? null : locationService.getModelById(dto.getLocationId());
        Resume resume = dto.getResumeId() == null ? null : resumeService.getModelById(dto.getResumeId());
        CoverLetter coverLetter = dto.getCoverLetterId() == null ? null : coverLetterService.getModelById(dto.getCoverLetterId());
        JobSource jobSource = jobSourceService.getModelById(dto.getSourceId());

        JobApplication updated = JobApplicationMapper.updateEntityWithDTOInfo(jobApplication, dto, location, jobSource, resume, coverLetter);
        JobApplication saved = jobApplicationRepository.save(updated);
        logUpdate(saved);
        createTimelineEntry(
            saved,
            resolveUpdateEventType(previousStatus, saved.getStatus()),
            buildUpdateDescription(saved, previousStatus, saved.getStatus())
        );
        return JobApplicationMapper.toDetailDTO(saved);
    }

    @Override
    public String deleteById(UUID id) {
        JobApplication jobApplication = getModelById(id);
        jobApplication.softDelete();
        jobApplicationRepository.save(jobApplication);
        logDelete(jobApplication);
        return "No Content";
    }

    private Location resolveLocation(JobApplicationCreateDTO dto) {
        if (dto.getLocationId() != null) {
            return locationService.getModelById(dto.getLocationId());
        }

        boolean hasLocationCity = dto.getLocationCity() != null && !dto.getLocationCity().isBlank();
        boolean hasLocationCountry = dto.getLocationCountry() != null && !dto.getLocationCountry().isBlank();

        if (hasLocationCity != hasLocationCountry) {
            throw new IllegalArgumentException("Both locationCity and locationCountry are required when creating an inline location.");
        }

        if (!hasLocationCity) {
            return null;
        }

        return locationService.findOrCreate(dto.getLocationCity(), dto.getLocationCountry());
    }

    private ApplicationTimeline.EventType resolveUpdateEventType(Status previousStatus, Status currentStatus) {
        if (currentStatus == Status.APPLIED && previousStatus != Status.APPLIED) {
            return ApplicationTimeline.EventType.APPLICATION_SUBMITTED;
        }
        return ApplicationTimeline.EventType.APPLICATION_UPDATED;
    }

    private String buildUpdateDescription(JobApplication jobApplication, Status previousStatus, Status currentStatus) {
        if (previousStatus != currentStatus) {
            return "Application status changed from " + previousStatus + " to " + currentStatus;
        }
        return "Application updated for " + jobApplication.getCompany() + " - " + jobApplication.getJobTitle();
    }

    private void createTimelineEntry(JobApplication jobApplication, ApplicationTimeline.EventType eventType, String description) {
        if (applicationTimelineRepository == null) {
            return;
        }

        ApplicationTimeline timeline = new ApplicationTimeline(eventType, jobApplication, LocalDateTime.now(), description);
        applicationTimelineRepository.save(timeline);
    }

    private void logCreate(JobApplication jobApplication) {
        if (auditLogService != null) {
            auditLogService.logCreate("Created job application with id: " + jobApplication.getId());
        }
    }

    private void logUpdate(JobApplication jobApplication) {
        if (auditLogService != null) {
            auditLogService.logUpdate("Updated job application with id: " + jobApplication.getId());
        }
    }

    private void logDelete(JobApplication jobApplication) {
        if (auditLogService != null) {
            auditLogService.logDelete("Deleted job application with id: " + jobApplication.getId());
        }
    }

    private User getCurrentUser() {
        return SecurityUtils.getCurrentUserOrThrow(userRepository);
    }
}
