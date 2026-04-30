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

import com.nick.job_application_tracker.dto.create.ResumeCreateDTO;
import com.nick.job_application_tracker.dto.response.ResumeResponseDTO;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.inter_face.ResumeRepository;
import com.nick.job_application_tracker.service.implementation.ResumeService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

public class ResumeServiceTest {

    private ResumeRepository resumeRepository;
    private ResumeService resumeService;
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        resumeRepository = mock(ResumeRepository.class);
        auditLogService = mock(AuditLogService.class);
        resumeService = new ResumeService(resumeRepository, auditLogService);
    }

    @Test
    @DisplayName("Should return all resumes")
    void testFindAll() {
        Resume resume = new Resume();
        resume.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        resume.setFilePath("/path/resume1.pdf");

        when(resumeRepository.findAll()).thenReturn(List.of(resume));

        List<ResumeResponseDTO> resumes = resumeService.findAll();

        assertThat(resumes).hasSize(1);
        assertThat(resumes.get(0).getFilePath()).isEqualTo("/path/resume1.pdf");
    }

    @Test
    @DisplayName("Should create and return a resume")
    void testCreate() {
        ResumeCreateDTO dto = new ResumeCreateDTO();
        dto.setTitle("Resume");
        dto.setFilePath("/new/path/resume.pdf");

        Resume saved = new Resume();
        saved.setId(com.nick.job_application_tracker.TestIds.uuid(10));
        saved.setTitle("Resume");
        saved.setFilePath("/new/path/resume.pdf");

        when(resumeRepository.save(any(Resume.class))).thenReturn(saved);

        ResumeResponseDTO result = resumeService.create(dto);

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getFilePath()).isEqualTo("/new/path/resume.pdf");
    }

    @Test
    @DisplayName("Should delete a resume by ID")
    void testDelete() {
        resumeService.delete(com.nick.job_application_tracker.TestIds.uuid(5));
        verify(resumeRepository).deleteById(com.nick.job_application_tracker.TestIds.uuid(5));
    }
}
