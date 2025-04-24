package com.nick.job_application_tracker.service;

import com.nick.job_application_tracker.dto.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.JobSourceDTO;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.JobSourceRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class JobSourceServiceTest {

    private JobSourceRepository jobSourceRepository;
    private JobSourceService jobSourceService;

    @BeforeEach
    void setup() {
        jobSourceRepository = mock(JobSourceRepository.class);
        jobSourceService = new JobSourceService(jobSourceRepository);
    }

    @Test
    @DisplayName("Should return all job sources")
    void testGetAllSources() {
        JobSource s1 = new JobSource();
        s1.setId(1L);
        s1.setName("LinkedIn");

        JobSource s2 = new JobSource();
        s2.setId(2L);
        s2.setName("Company Website");

        when(jobSourceRepository.findAll()).thenReturn(List.of(s1, s2));

        List<JobSourceDTO> sources = jobSourceService.getAllSources();

        assertThat(sources).hasSize(2);
        assertThat(sources).anyMatch(s -> s.getName().equals("LinkedIn"));
    }

    @Test
    @DisplayName("Should create a new job source")
    void testCreateSource() {
        JobSourceCreateDTO dto = new JobSourceCreateDTO();
        dto.setName("Indeed");

        JobSource saved = new JobSource();
        saved.setId(1L);
        saved.setName("Indeed");

        when(jobSourceRepository.save(any(JobSource.class))).thenReturn(saved);

        JobSourceDTO result = jobSourceService.createSource(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Indeed");
    }

    @Test
    @DisplayName("Should return job source by ID")
    void testGetSourceById() {
        JobSource source = new JobSource();
        source.setId(1L);
        source.setName("AngelList");

        when(jobSourceRepository.findById(1L)).thenReturn(Optional.of(source));

        Optional<JobSourceDTO> result = jobSourceService.getSourceById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("AngelList");
    }

    @Test
    @DisplayName("Should return empty Optional when job source not found by ID")
    void testGetSourceByInvalidId() {
        when(jobSourceRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<JobSourceDTO> result = jobSourceService.getSourceById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update existing job source")
    void testUpdateSource() {
        JobSource existing = new JobSource();
        existing.setId(1L);
        existing.setName("Old Name");

        JobSourceCreateDTO updateDTO = new JobSourceCreateDTO();
        updateDTO.setName("New Name");

        when(jobSourceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(jobSourceRepository.save(any(JobSource.class))).thenAnswer(i -> i.getArgument(0));

        Optional<JobSourceDTO> result = jobSourceService.updateSource(1L, updateDTO);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should return empty Optional when updating non-existing job source")
    void testUpdateNonExistingSource() {
        when(jobSourceRepository.findById(100L)).thenReturn(Optional.empty());

        Optional<JobSourceDTO> result = jobSourceService.updateSource(100L, new JobSourceCreateDTO());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete job source by ID")
    void testDeleteSource() {
        jobSourceService.deleteSource(1L);
        verify(jobSourceRepository).deleteById(1L);
    }
}
