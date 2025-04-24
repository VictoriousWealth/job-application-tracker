package com.nick.job_application_tracker.repository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.JobApplication.Status;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.model.User;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class SkillTrackerRepositoryTest {

    @Autowired
    private SkillTrackerRepository skillRepo;

    @Autowired
    private JobApplicationRepository jobAppRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @DisplayName("Should save and retrieve skills by JobApplication ID")
    public void testFindByJobApplicationId() {
        User user = new User();
        user.setEmail("skilltest@example.com");
        user.setPassword("test123");
        user.setEnabled(true);
        user = userRepo.save(user);

        JobApplication jobApp = new JobApplication();
        jobApp.setJobTitle("Backend Dev");
        jobApp.setCompany("DevCorp");
        jobApp.setStatus(Status.APPLIED);
        jobApp.setUser(user);
        jobApp = jobAppRepo.save(jobApp);

        SkillTracker javaSkill = new SkillTracker();
        javaSkill.setJobApplication(jobApp);
        javaSkill.setSkillName("Java");

        SkillTracker sqlSkill = new SkillTracker();
        sqlSkill.setJobApplication(jobApp);
        sqlSkill.setSkillName("SQL");

        skillRepo.saveAll(List.of(javaSkill, sqlSkill));

        List<SkillTracker> results = skillRepo.findByJobApplicationId(jobApp.getId());

        assertThat(results).hasSize(2);
        assertThat(results).extracting(SkillTracker::getSkillName)
                          .containsExactlyInAnyOrder("Java", "SQL");
    }

    @Test
    @DisplayName("Should return empty list if JobApplication ID not found")
    public void testFindByInvalidJobApplicationId() {
        List<SkillTracker> results = skillRepo.findByJobApplicationId(-999L);
        assertThat(results).isEmpty();
    }
}
