package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nick.job_application_tracker.dto.JobApplicationCreateDTO;
import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.dto.JobApplicationResponseDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.CoverLetterRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.JobSourceRepository;
import com.nick.job_application_tracker.repository.LocationRepository;
import com.nick.job_application_tracker.repository.ResumeRepository;

public class JobApplicationServiceTest {

    private JobApplicationRepository jobAppRepo;
    private LocationRepository locationRepo;
    private JobSourceRepository sourceRepo;
    private ResumeRepository resumeRepo;
    private CoverLetterRepository coverLetterRepo;
    private JobApplicationService service;
    private AuditLogService auditLogService;


    @BeforeEach
    void setup() {
        jobAppRepo = mock(JobApplicationRepository.class);
        locationRepo = mock(LocationRepository.class);
        sourceRepo = mock(JobSourceRepository.class);
        resumeRepo = mock(ResumeRepository.class);
        coverLetterRepo = mock(CoverLetterRepository.class);
        auditLogService = mock(AuditLogService.class);

        service = new JobApplicationService(jobAppRepo, locationRepo, sourceRepo, resumeRepo, coverLetterRepo, auditLogService);
    }

    @Test
    @DisplayName("Should create a new job application")
    void testCreateJobApplication() {
        JobApplicationCreateDTO dto = new JobApplicationCreateDTO();
        dto.setJobTitle("Engineer");
        dto.setCompany("TestCorp");
        dto.setLocationCity("London");
        dto.setLocationCountry("UK");

        Location location = new Location();
        location.setCity("London");
        location.setCountry("UK");

        when(locationRepo.findByCityAndCountry("London", "UK")).thenReturn(Optional.empty());
        when(locationRepo.save(any(Location.class))).thenReturn(location);
        when(jobAppRepo.save(any(JobApplication.class))).thenAnswer(i -> i.getArgument(0));

        JobApplicationResponseDTO result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getCompany()).isEqualTo("TestCorp");
        verify(jobAppRepo).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("Should throw error when source not found")
    void testCreateWithMissingSource() {
        JobApplicationCreateDTO dto = new JobApplicationCreateDTO();
        dto.setSourceId(99L);
        dto.setLocationCity("London");
        dto.setLocationCountry("UK");

        when(locationRepo.findByCityAndCountry(any(), any())).thenReturn(Optional.of(new Location()));
        when(sourceRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Source not found");
    }

    @Test
    @DisplayName("Should return all job applications")
    void testGetAll() {
        when(jobAppRepo.findAll()).thenReturn(List.of(new JobApplication()));
        List<JobApplicationResponseDTO> all = service.getAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    @DisplayName("Should return job application by ID")
    void testGetById() {
        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setCompany("TestCo");
        when(jobAppRepo.findById(1L)).thenReturn(Optional.of(app));

        JobApplicationDetailDTO dto = service.getById(1L);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw error if job app not found")
    void testGetByInvalidId() {
        when(jobAppRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(999L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Job Application not found");
    }

    @Test
    @DisplayName("Should delete a job application by ID")
    void testDelete() {
        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(1L);

        when(jobAppRepo.findById(1L)).thenReturn(Optional.of(jobApplication));

        service.delete(1L);

        verify(jobAppRepo).delete(jobApplication);
    }

}
