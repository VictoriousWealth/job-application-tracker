package com.nick.job_application_tracker.service;

import com.nick.job_application_tracker.dto.ResumeDTO;
import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.ResumeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ResumeServiceTest {

    private ResumeRepository resumeRepository;
    private ResumeService resumeService;

    @BeforeEach
    void setUp() {
        resumeRepository = mock(ResumeRepository.class);
        resumeService = new ResumeService(resumeRepository);
    }

    @Test
    @DisplayName("Should return all resumes")
    void testFindAll() {
        Resume r1 = new Resume();
        r1.setId(1L);
        r1.setFilePath("/path/resume1.pdf");

        Resume r2 = new Resume();
        r2.setId(2L);
        r2.setFilePath("/path/resume2.pdf");

        when(resumeRepository.findAll()).thenReturn(List.of(r1, r2));

        List<ResumeDTO> resumes = resumeService.findAll();

        assertThat(resumes).hasSize(2);
        assertThat(resumes.get(0).getFilePath()).isEqualTo("/path/resume1.pdf");
        assertThat(resumes.get(1).getFilePath()).isEqualTo("/path/resume2.pdf");
    }

    @Test
    @DisplayName("Should save and return a resume")
    void testSave() {
        ResumeDTO dto = new ResumeDTO(null, "/new/path/resume.pdf");
        Resume entity = new Resume();
        entity.setFilePath("/new/path/resume.pdf");

        Resume saved = new Resume();
        saved.setId(10L);
        saved.setFilePath("/new/path/resume.pdf");

        when(resumeRepository.save(any(Resume.class))).thenReturn(saved);

        ResumeDTO result = resumeService.save(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getFilePath()).isEqualTo("/new/path/resume.pdf");
    }

    @Test
    @DisplayName("Should delete a resume by ID")
    void testDelete() {
        resumeService.delete(5L);
        verify(resumeRepository, times(1)).deleteById(5L);
    }
}
