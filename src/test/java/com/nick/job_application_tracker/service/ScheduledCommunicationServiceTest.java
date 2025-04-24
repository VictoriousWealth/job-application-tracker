package com.nick.job_application_tracker.service;

import com.nick.job_application_tracker.dto.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.ScheduledCommunicationDTO;
import com.nick.job_application_tracker.mapper.ScheduledCommunicationMapper;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.ScheduledCommunication.Type;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.repository.ScheduledCommunicationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScheduledCommunicationServiceTest {

    private ScheduledCommunicationRepository repository;
    private JobApplicationRepository jobAppRepo;
    private ScheduledCommunicationService service;

    @BeforeEach
    void setUp() {
        repository = mock(ScheduledCommunicationRepository.class);
        jobAppRepo = mock(JobApplicationRepository.class);
        service = new ScheduledCommunicationService(repository, jobAppRepo);
    }

    @Test
    @DisplayName("Should return all scheduled communications")
    void testGetAll() {
        ScheduledCommunication sc1 = new ScheduledCommunication();
        sc1.setId(1L);
        sc1.setType(Type.CALL);
        sc1.setScheduledFor(LocalDateTime.now());

        ScheduledCommunication sc2 = new ScheduledCommunication();
        sc2.setId(2L);
        sc2.setType(Type.INTERVIEW);
        sc2.setScheduledFor(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(sc1, sc2));

        List<ScheduledCommunicationDTO> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(dto -> dto.getType().equals("CALL"));
    }

    @Test
    @DisplayName("Should return scheduled communication by ID")
    void testGetById() {
        ScheduledCommunication sc = new ScheduledCommunication();
        sc.setId(1L);
        sc.setType(Type.INTERVIEW);
        sc.setScheduledFor(LocalDateTime.of(2025, 5, 1, 12, 0));

        when(repository.findById(1L)).thenReturn(Optional.of(sc));

        ScheduledCommunicationDTO result = service.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getType()).isEqualTo("INTERVIEW");
    }

    @Test
    @DisplayName("Should throw error if scheduled communication not found")
    void testGetByIdNotFound() {
        when(repository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(100L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("ScheduledCommunication not found");
    }

    @Test
    @DisplayName("Should create a scheduled communication")
    void testCreate() {
        JobApplication jobApp = new JobApplication();
        jobApp.setId(1L);

        ScheduledCommunicationCreateDTO dto = new ScheduledCommunicationCreateDTO();
        dto.setType("INTERVIEW");
        dto.setNotes("Technical interview");
        dto.setScheduledFor(LocalDateTime.of(2025, 5, 10, 15, 0));
        dto.setJobApplicationId(1L);

        ScheduledCommunication saved = ScheduledCommunicationMapper.toEntity(dto, jobApp);
        saved.setId(99L);

        when(jobAppRepo.findById(1L)).thenReturn(Optional.of(jobApp));
        when(repository.save(any(ScheduledCommunication.class))).thenReturn(saved);

        ScheduledCommunicationDTO result = service.create(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(99L);
        assertThat(result.getType()).isEqualTo("INTERVIEW");
    }

    @Test
    @DisplayName("Should delete scheduled communication by ID")
    void testDelete() {
        service.delete(15L);
        verify(repository).deleteById(15L);
    }
}
