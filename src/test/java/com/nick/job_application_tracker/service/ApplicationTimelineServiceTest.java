package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.nick.job_application_tracker.dto.create.ApplicationTimelineCreateDTO;
import com.nick.job_application_tracker.dto.detail.ApplicationTimelineDetailDTO;
import com.nick.job_application_tracker.dto.response.ApplicationTimelineResponseDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.inter_face.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.service.implementation.ApplicationTimelineService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

@ExtendWith(MockitoExtension.class)
public class ApplicationTimelineServiceTest {

    private static final UUID JOB_APP_ID = UUID.fromString("00000000-0000-0000-0000-000000000601");
    private static final UUID TIMELINE_ID = UUID.fromString("00000000-0000-0000-0000-000000000602");

    @Mock
    private ApplicationTimelineRepository timelineRepo;

    @Mock
    private JobApplicationRepository jobRepo;

    @Mock
    private AuditLogService auditLogService;

    private ApplicationTimelineService service;

    @BeforeEach
    void setup() {
        service = new ApplicationTimelineService(timelineRepo, auditLogService, jobRepo);
    }

    @Test
    @DisplayName("Should create timeline via DTO and return detail DTO")
    void testCreateDto() {
        ApplicationTimelineCreateDTO dto = new ApplicationTimelineCreateDTO();
        dto.setEventType(ApplicationTimeline.EventType.CREATED);
        dto.setEventTime(LocalDateTime.now());
        dto.setDescription("Submitted application");
        dto.setJobApplicationId(JOB_APP_ID);

        JobApplication jobApplication = new JobApplication();
        jobApplication.setId(JOB_APP_ID);

        ApplicationTimeline savedEntity = new ApplicationTimeline();
        savedEntity.setId(TIMELINE_ID);
        savedEntity.setEventType(ApplicationTimeline.EventType.CREATED);
        savedEntity.setEventTime(dto.getEventTime());
        savedEntity.setDescription(dto.getDescription());
        savedEntity.setJobApplication(jobApplication);

        when(jobRepo.findById(JOB_APP_ID)).thenReturn(Optional.of(jobApplication));
        when(timelineRepo.save(any(ApplicationTimeline.class))).thenReturn(savedEntity);

        ApplicationTimelineDetailDTO saved = service.create(dto);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(TIMELINE_ID);
        assertThat(saved.getDescription()).isEqualTo("Submitted application");
    }

    @Test
    @DisplayName("Should return list of timelines by job application ID")
    void testGetByJobAppId() {
        ApplicationTimeline savedEntity = new ApplicationTimeline();
        savedEntity.setId(TIMELINE_ID);
        savedEntity.setEventType(ApplicationTimeline.EventType.UPDATED);
        savedEntity.setEventTime(LocalDateTime.now());
        savedEntity.setDescription("Updated CV");

        when(timelineRepo.findByJobApplicationIdAndDeletedFalse(JOB_APP_ID, Pageable.unpaged()))
            .thenReturn(new PageImpl<>(List.of(savedEntity)));

        List<ApplicationTimelineResponseDTO> list = service.getByJobAppId(JOB_APP_ID);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getId()).isEqualTo(TIMELINE_ID);
    }

    @Test
    @DisplayName("Should save timeline entity and persist it")
    void testSaveEntity() {
        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setEventType(ApplicationTimeline.EventType.UPDATED);
        entity.setEventTime(LocalDateTime.now());
        entity.setDescription("Updated CV");

        ApplicationTimeline savedEntity = new ApplicationTimeline();
        savedEntity.setId(TIMELINE_ID);
        savedEntity.setEventType(entity.getEventType());
        savedEntity.setEventTime(entity.getEventTime());
        savedEntity.setDescription(entity.getDescription());

        when(timelineRepo.save(any(ApplicationTimeline.class))).thenReturn(savedEntity);

        ApplicationTimeline saved = service.save(entity);

        assertThat(saved.getId()).isEqualTo(TIMELINE_ID);
    }
}
