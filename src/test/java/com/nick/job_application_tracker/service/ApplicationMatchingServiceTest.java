package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.response.ApplicationMatchResponseDTO;
import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.service.implementation.ApplicationMatchingService;
import com.nick.job_application_tracker.service.implementation.JobApplicationService;
import com.nick.job_application_tracker.service.implementation.WorkspaceReadService;

class ApplicationMatchingServiceTest {

    private JobApplicationService jobApplicationService;
    private WorkspaceReadService workspaceReadService;
    private ApplicationMatchingService service;

    @BeforeEach
    void setUp() {
        jobApplicationService = mock(JobApplicationService.class);
        workspaceReadService = mock(WorkspaceReadService.class);
        service = new ApplicationMatchingService(jobApplicationService, workspaceReadService);
    }

    @Test
    void matchApplicationFindsDocumentAndSkillOverlap() {
        JobApplication application = new JobApplication();
        application.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        application.setCompany("OpenAI");
        application.setJobTitle("Backend Engineer");
        application.setJobDescription("Looking for Java Spring Docker Kubernetes backend experience.");

        SkillTracker java = new SkillTracker();
        java.setSkillName("Java");
        SkillTracker spring = new SkillTracker();
        spring.setSkillName("Spring");

        Resume resume = new Resume("Java Backend Resume", "/docs/java-backend-resume.pdf");
        resume.setId(com.nick.job_application_tracker.TestIds.uuid(2));
        CoverLetter coverLetter = new CoverLetter();
        coverLetter.setId(com.nick.job_application_tracker.TestIds.uuid(3));
        coverLetter.setTitle("Spring Cover Letter");
        coverLetter.setFilePath("/docs/spring-cover-letter.pdf");
        coverLetter.setContent("Spring APIs, distributed systems, backend services.");

        when(jobApplicationService.getModelById(com.nick.job_application_tracker.TestIds.uuid(1))).thenReturn(application);
        when(workspaceReadService.getSkills()).thenReturn(List.of(java, spring));
        when(workspaceReadService.getResumes()).thenReturn(List.of(resume));
        when(workspaceReadService.getCoverLetters()).thenReturn(List.of(coverLetter));

        ApplicationMatchResponseDTO result = service.matchApplication(com.nick.job_application_tracker.TestIds.uuid(1));

        assertThat(result.company()).isEqualTo("OpenAI");
        assertThat(result.matchedSkills()).contains("Java", "Spring");
        assertThat(result.keywordMatches()).contains("java", "spring");
        assertThat(result.missingSkills()).contains("docker");
        assertThat(result.recommendedResume()).isNotNull();
        assertThat(result.recommendedResume().title()).isEqualTo("Java Backend Resume");
        assertThat(result.overallScore()).isGreaterThan(0.0);
    }
}
