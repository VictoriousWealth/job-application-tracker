package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.create.JobSourceCreateDTO;
import com.nick.job_application_tracker.dto.detail.JobSourceDetailDTO;
import com.nick.job_application_tracker.dto.response.JobSourceResponseDTO;
import com.nick.job_application_tracker.dto.update.JobSourceUpdateDTO;
import com.nick.job_application_tracker.mapper.JobSourceMapper;
import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.inter_face.JobSourceRepository;
import com.nick.job_application_tracker.service.implementation.JobSourceService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

public class JobSourceServiceTest {

    private static final UUID SOURCE_ID = UUID.fromString("00000000-0000-0000-0000-000000000501");

    private JobSourceRepository jobSourceRepository;
    private JobSourceService jobSourceService;
    private AuditLogService auditLogService;

    @BeforeEach
    void setup() {
        jobSourceRepository = mock(JobSourceRepository.class);
        auditLogService = mock(AuditLogService.class);
        jobSourceService = new JobSourceService(jobSourceRepository, new JobSourceMapper(), auditLogService);
    }

    @Test
    @DisplayName("Should return all job sources")
    void testGetAllSources() {
        JobSource source = new JobSource();
        source.setId(SOURCE_ID);
        source.setName("LinkedIn");

        when(jobSourceRepository.findAll()).thenReturn(List.of(source));

        List<JobSourceResponseDTO> sources = jobSourceService.getAllSources();

        assertThat(sources).hasSize(1);
        assertThat(sources.get(0).getId()).isEqualTo(SOURCE_ID);
        assertThat(sources.get(0).getName()).isEqualTo("LinkedIn");
    }

    @Test
    @DisplayName("Should create a new job source")
    void testCreateSource() {
        JobSourceCreateDTO dto = new JobSourceCreateDTO();
        dto.setName("Indeed");

        JobSource saved = new JobSource();
        saved.setId(SOURCE_ID);
        saved.setName("Indeed");

        when(jobSourceRepository.save(any(JobSource.class))).thenReturn(saved);

        JobSourceResponseDTO result = jobSourceService.createSource(dto);

        assertThat(result.getId()).isEqualTo(SOURCE_ID);
        assertThat(result.getName()).isEqualTo("Indeed");
    }

    @Test
    @DisplayName("Should return job source by ID")
    void testGetSourceById() {
        JobSource source = new JobSource();
        source.setId(SOURCE_ID);
        source.setName("AngelList");

        when(jobSourceRepository.findById(SOURCE_ID)).thenReturn(Optional.of(source));

        Optional<JobSourceDetailDTO> result = jobSourceService.getSourceById(SOURCE_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("AngelList");
    }

    @Test
    @DisplayName("Should update existing job source")
    void testUpdateSource() {
        JobSource existing = new JobSource();
        existing.setId(SOURCE_ID);
        existing.setName("Old Name");

        JobSourceUpdateDTO updateDTO = new JobSourceUpdateDTO();
        updateDTO.setName("New Name");

        when(jobSourceRepository.findById(SOURCE_ID)).thenReturn(Optional.of(existing));
        when(jobSourceRepository.save(any(JobSource.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<JobSourceDetailDTO> result = jobSourceService.updateSource(SOURCE_ID, updateDTO);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name");
    }

    @Test
    @DisplayName("Should delete job source by ID")
    void testDeleteSource() {
        jobSourceService.deleteSource(SOURCE_ID);
        verify(jobSourceRepository).deleteById(SOURCE_ID);
    }
}
