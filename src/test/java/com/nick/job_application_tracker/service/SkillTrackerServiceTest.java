package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.nick.job_application_tracker.dto.create.SkillTrackerCreateDTO;
import com.nick.job_application_tracker.dto.response.SkillTrackerResponseDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.inter_face.SkillTrackerRepository;
import com.nick.job_application_tracker.service.implementation.SkillTrackerService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.service.specialised_common.JobApplicationServiceInterface;

public class SkillTrackerServiceTest {

    private SkillTrackerRepository repo;
    private JobApplicationServiceInterface jobApplicationService;
    private SkillTrackerService service;
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        repo = mock(SkillTrackerRepository.class);
        jobApplicationService = mock(JobApplicationServiceInterface.class);
        auditLogService = mock(AuditLogService.class);
        service = new SkillTrackerService(repo, jobApplicationService, auditLogService);
    }

    @Test
    @DisplayName("Should get all skills by job application ID")
    void testGetByJobAppId() {
        SkillTracker skill = new SkillTracker();
        skill.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        skill.setSkillName("Java");

        when(repo.findByJobApplicationIdAndDeletedFalse(com.nick.job_application_tracker.TestIds.uuid(10), Pageable.unpaged()))
            .thenReturn(new PageImpl<>(List.of(skill)));

        List<SkillTrackerResponseDTO> skills = service.getByJobAppId(com.nick.job_application_tracker.TestIds.uuid(10));

        assertThat(skills).hasSize(1);
        assertThat(skills.get(0).getSkillName()).isEqualTo("Java");
    }

    @Test
    @DisplayName("Should create a skill tracker entry")
    void testCreate() {
        JobApplication job = new JobApplication();
        job.setId(com.nick.job_application_tracker.TestIds.uuid(1));

        SkillTrackerCreateDTO dto = new SkillTrackerCreateDTO();
        dto.setSkillName("Docker");
        dto.setJobApplicationId(job.getId());

        SkillTracker saved = new SkillTracker();
        saved.setId(com.nick.job_application_tracker.TestIds.uuid(55));
        saved.setSkillName("Docker");
        saved.setJobApplication(job);

        when(jobApplicationService.getModelById(job.getId())).thenReturn(job);
        when(repo.save(any(SkillTracker.class))).thenReturn(saved);

        SkillTrackerResponseDTO result = service.create(dto);

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getJobApplicationId()).isEqualTo(job.getId());
    }

    @Test
    @DisplayName("Should delete a skill tracker entry by ID")
    void testDelete() {
        service.delete(com.nick.job_application_tracker.TestIds.uuid(77));
        verify(repo).deleteById(com.nick.job_application_tracker.TestIds.uuid(77));
    }
}
