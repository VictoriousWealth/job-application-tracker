package com.nick.job_application_tracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.dto.ApplicationTimelineDTO;
import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.ApplicationTimelineRepository;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTimelineServiceTest {

    @Autowired
    private ApplicationTimelineService service;

    @Autowired
    private ApplicationTimelineRepository timelineRepo;

    @Autowired
    private JobApplicationRepository jobRepo;

    @Autowired
    private UserRepository userRepo;

    private JobApplication job;

    @BeforeEach
    void setup() {
        timelineRepo.deleteAll();
        jobRepo.deleteAll();
        userRepo.deleteAll();

        User user = new User();
        user.setEmail("timelineuser@example.com");
        user.setPassword("pass");
        user.setEnabled(true);
        userRepo.save(user);

        job = new JobApplication();
        job.setUser(user);
        job.setCompany("TestCorp");
        job.setJobTitle("Engineer");
        job.setStatus(JobApplication.Status.APPLIED);
        job = jobRepo.save(job);
    }

    @Test
    @Transactional
    @DisplayName("Should save timeline via DTO and return DTO")
    void testSaveDto() {
        ApplicationTimelineDTO dto = new ApplicationTimelineDTO(
            null,
            "CREATED",
            LocalDateTime.now(),
            "Submitted application",
            job.getId()
        );

        ApplicationTimelineDTO saved = service.save(dto);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Submitted application");
    }

    @Test
    @Transactional
    @DisplayName("Should return list of timelines by job application ID")
    void testGetByJobAppId() {
        ApplicationTimeline timeline = new ApplicationTimeline();
        timeline.setJobApplication(job);
        timeline.setEventType(ApplicationTimeline.EventType.CREATED);
        timeline.setEventTime(LocalDateTime.now());
        timeline.setDescription("Created");
        timelineRepo.save(timeline);

        List<ApplicationTimelineDTO> list = service.getByJobAppId(job.getId());

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getDescription()).isEqualTo("Created");
    }

    @Test
    @Transactional
    @DisplayName("Should save timeline entity and persist it")
    void testSaveEntity() {
        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setJobApplication(job);
        entity.setEventType(ApplicationTimeline.EventType.UPDATED);
        entity.setEventTime(LocalDateTime.now());
        entity.setDescription("Updated CV");

        ApplicationTimeline saved = service.save(entity);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDescription()).isEqualTo("Updated CV");
    }

    @Test
    @Transactional
    @DisplayName("Should delete timeline by ID")
    void testDelete() {
        ApplicationTimeline entity = new ApplicationTimeline();
        entity.setJobApplication(job);
        entity.setEventType(ApplicationTimeline.EventType.CANCELLED);
        entity.setEventTime(LocalDateTime.now());
        entity.setDescription("Withdrawn");
        entity = timelineRepo.save(entity);

        Long id = entity.getId();
        service.delete(id);

        Optional<ApplicationTimeline> result = timelineRepo.findById(id);
        assertThat(result).isEmpty();
    }
}
