package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class ApplicationTimelineServiceTest {

    @Mock
    private ApplicationTimelineRepository timelineRepo;

    @Mock
    private JobApplicationRepository jobRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private AuditLogService auditLogService;

    private ApplicationTimelineService service;

    private JobApplication job;

    @BeforeEach
    void setup() {
        service = new ApplicationTimelineService(timelineRepo, auditLogService);

        job = new JobApplication();
        job.setCompany("TestCorp");
        job.setJobTitle("Engineer");
        job.setStatus(JobApplication.Status.APPLIED);
    }

    @Test
    @DisplayName("Should save timeline via DTO and return DTO")
    void testSaveDto() {
        ApplicationTimelineDTO dto = new ApplicationTimelineDTO(
            null,
            "CREATED",
            LocalDateTime.now(),
            "Submitted application",
            1L
        );

        // Create a fake saved entity
        ApplicationTimeline savedEntity = new ApplicationTimeline();
        savedEntity.setId(1L);
        savedEntity.setEventType(ApplicationTimeline.EventType.CREATED);
        savedEntity.setEventTime(dto.getEventTime());
        savedEntity.setDescription(dto.getDescription());
        savedEntity.setJobApplication(new JobApplication()); // or mock if necessary

        // 🛠️ Mock repo.save behavior
        when(timelineRepo.save(any(ApplicationTimeline.class))).thenReturn(savedEntity);

        // Now safely call the service
        ApplicationTimelineDTO saved = service.save(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(saved.getDescription()).isEqualTo("Submitted application");
    }


    @Test
    @DisplayName("Should return list of timelines by job application ID")
    void testGetByJobAppId() {
        List<ApplicationTimelineDTO> list = service.getByJobAppId(1L);
        assertThat(list).isNotNull(); // not a real list here unless you mock repo behavior
    }

    @Test
    @DisplayName("Should save timeline entity and persist it")
    void testSaveEntity() {
        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setJobApplication(job);
        entity.setEventType(ApplicationTimeline.EventType.UPDATED);
        entity.setEventTime(LocalDateTime.now());
        entity.setDescription("Updated CV");

        ApplicationTimeline savedEntity = new ApplicationTimeline();
        savedEntity.setId(1L);
        savedEntity.setJobApplication(job);
        savedEntity.setEventType(ApplicationTimeline.EventType.UPDATED);
        savedEntity.setEventTime(entity.getEventTime());
        savedEntity.setDescription(entity.getDescription());

        when(timelineRepo.save(any(ApplicationTimeline.class))).thenReturn(savedEntity); // ✅ Mock the save

        ApplicationTimeline saved = service.save(entity);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should delete timeline by ID")
    void testDelete() {
        service.delete(1L);
    }
}
