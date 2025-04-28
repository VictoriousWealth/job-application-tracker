package com.nick.job_application_tracker.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.SkillTrackerRepository;

public class SkillTrackerServiceTest {

    private SkillTrackerRepository repo;
    private SkillTrackerService service;
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        repo = mock(SkillTrackerRepository.class);
        auditLogService = mock(AuditLogService.class);
        service = new SkillTrackerService(repo, auditLogService);
    }

    @Test
    @DisplayName("Should get all skills by job application ID")
    void testGetByJobAppId() {
        SkillTracker skill1 = new SkillTracker();
        skill1.setId(1L);
        skill1.setSkillName("Java");

        SkillTracker skill2 = new SkillTracker();
        skill2.setId(2L);
        skill2.setSkillName("Spring Boot");

        when(repo.findByJobApplicationId(10L)).thenReturn(List.of(skill1, skill2));

        List<SkillTracker> skills = service.getByJobAppId(10L);

        assertThat(skills).hasSize(2);
        assertThat(skills).anyMatch(s -> s.getSkillName().equals("Java"));
    }

    @Test
    @DisplayName("Should save a skill tracker entry")
    void testSave() {
        SkillTracker skill = new SkillTracker();
        skill.setSkillName("Docker");

        // 🛠 FIX: Add a JobApplication
        JobApplication job = new JobApplication();
        job.setId(1L); // (mocked ID)
        skill.setJobApplication(job);

        SkillTracker saved = new SkillTracker();
        saved.setId(55L);
        saved.setSkillName("Docker");
        saved.setJobApplication(job); // Match saved skill too

        when(repo.save(skill)).thenReturn(saved);

        SkillTracker result = service.save(skill);

        assertThat(result.getId()).isEqualTo(55L);
        assertThat(result.getSkillName()).isEqualTo("Docker");
        assertThat(result.getJobApplication().getId()).isEqualTo(1L); // ✅ Assert jobApp too if you want
    }

    @Test
    @DisplayName("Should delete a skill tracker entry by ID")
    void testDelete() {
        Long id = 77L;
        service.delete(id);
        verify(repo).deleteById(id);
    }
}
