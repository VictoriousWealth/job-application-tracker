package com.nick.job_application_tracker.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Role;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@TestPropertySource("classpath:application-test.properties") 
@Transactional
@SpringBootTest
public class ApplicationTimelineRepositoryTest {

    @Autowired
    private ApplicationTimelineRepository timelineRepo;

    @Autowired
    private JobApplicationRepository jobApplicationRepo;

    @Autowired
    private UserRepository userRepo;

    private JobApplication createJobAppWithUser() {
        User user = new User();
        user.setEmail("multi@example.com");
        user.setPassword("securepassword");
        user.setEnabled(true);
        user.setRole(Role.BASIC);
        user = userRepo.save(user);

        JobApplication jobApp = new JobApplication();
        jobApp.setJobTitle("Backend Developer");
        jobApp.setCompany("SpringSoft");
        jobApp.setStatus(JobApplication.Status.APPLIED);
        jobApp.setUser(user);
        return jobApplicationRepo.save(jobApp);
    }

    @Test
    @DisplayName("Should return multiple timelines for a single JobApplication")
    public void shouldReturnMultipleTimelines() {
        JobApplication jobApp = createJobAppWithUser();

        ApplicationTimeline t1 = new ApplicationTimeline();
        t1.setJobApplication(jobApp);
        t1.setEventType(ApplicationTimeline.EventType.CREATED);
        t1.setEventTime(LocalDateTime.now().minusDays(2));
        t1.setDescription("Application Created");

        ApplicationTimeline t2 = new ApplicationTimeline();
        t2.setJobApplication(jobApp);
        t2.setEventType(ApplicationTimeline.EventType.SUBMITTED);
        t2.setEventTime(LocalDateTime.now());
        t2.setDescription("Application Submitted");

        timelineRepo.saveAll(List.of(t1, t2));

        List<ApplicationTimeline> timelines = timelineRepo.findByJobApplicationId(jobApp.getId());

        assertThat(timelines).hasSize(2);
        assertThat(timelines).extracting("description")
                             .containsExactlyInAnyOrder("Application Created", "Application Submitted");
    }

    @Test
    @DisplayName("Should return empty list when no timelines exist for given JobApplication ID")
    public void shouldReturnEmptyListForInvalidJobAppId() {
        List<ApplicationTimeline> timelines = timelineRepo.findByJobApplicationId(99999L); // unlikely to exist
        assertThat(timelines).isEmpty();
    }
}
