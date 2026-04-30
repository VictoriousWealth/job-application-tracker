package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.create.ScheduledCommunicationCreateDTO;
import com.nick.job_application_tracker.dto.detail.ScheduledCommunicationDetailDTO;
import com.nick.job_application_tracker.dto.response.ScheduledCommunicationResponseDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.model.ScheduledCommunication.Type;
import com.nick.job_application_tracker.repository.inter_face.JobApplicationRepository;
import com.nick.job_application_tracker.repository.inter_face.ScheduledCommunicationRepository;
import com.nick.job_application_tracker.service.implementation.ScheduledCommunicationService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

public class ScheduledCommunicationServiceTest {

    private ScheduledCommunicationRepository repository;
    private JobApplicationRepository jobAppRepo;
    private ScheduledCommunicationService service;
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        repository = mock(ScheduledCommunicationRepository.class);
        jobAppRepo = mock(JobApplicationRepository.class);
        auditLogService = mock(AuditLogService.class);
        service = new ScheduledCommunicationService(repository, jobAppRepo, auditLogService);
    }

    @Test
    @DisplayName("Should return all scheduled communications")
    void testGetAll() {
        ScheduledCommunication entity = new ScheduledCommunication();
        entity.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        entity.setType(Type.INTERVIEW);
        entity.setScheduledFor(LocalDateTime.now());

        when(repository.findAll()).thenReturn(List.of(entity));

        List<ScheduledCommunicationResponseDTO> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getType()).isEqualTo("INTERVIEW");
    }

    @Test
    @DisplayName("Should return scheduled communication by ID")
    void testGetById() {
        ScheduledCommunication entity = new ScheduledCommunication();
        entity.setId(com.nick.job_application_tracker.TestIds.uuid(1));
        entity.setType(Type.INTERVIEW);
        entity.setScheduledFor(LocalDateTime.of(2025, 5, 1, 12, 0));

        when(repository.findById(com.nick.job_application_tracker.TestIds.uuid(1))).thenReturn(Optional.of(entity));

        ScheduledCommunicationDetailDTO result = service.getById(com.nick.job_application_tracker.TestIds.uuid(1));

        assertThat(result.getType()).isEqualTo("INTERVIEW");
    }

    @Test
    @DisplayName("Should throw error if scheduled communication not found")
    void testGetByIdNotFound() {
        when(repository.findById(com.nick.job_application_tracker.TestIds.uuid(100))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(com.nick.job_application_tracker.TestIds.uuid(100)))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("ScheduledCommunication not found");
    }

    @Test
    @DisplayName("Should create a scheduled communication")
    void testCreate() {
        JobApplication jobApp = new JobApplication();
        jobApp.setId(com.nick.job_application_tracker.TestIds.uuid(1));

        ScheduledCommunicationCreateDTO dto = new ScheduledCommunicationCreateDTO();
        dto.setType("INTERVIEW");
        dto.setNotes("Technical interview");
        dto.setScheduledFor(LocalDateTime.of(2025, 5, 10, 15, 0));
        dto.setJobApplicationId(jobApp.getId());

        ScheduledCommunication saved = new ScheduledCommunication();
        saved.setId(com.nick.job_application_tracker.TestIds.uuid(99));
        saved.setType(Type.INTERVIEW);
        saved.setScheduledFor(dto.getScheduledFor());
        saved.setNotes(dto.getNotes());
        saved.setJobApplication(jobApp);

        when(jobAppRepo.findById(jobApp.getId())).thenReturn(Optional.of(jobApp));
        when(repository.save(any(ScheduledCommunication.class))).thenReturn(saved);

        ScheduledCommunicationResponseDTO result = service.create(dto);

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getType()).isEqualTo("INTERVIEW");
    }

    @Test
    @DisplayName("Should delete scheduled communication by ID")
    void testDelete() {
        service.delete(com.nick.job_application_tracker.TestIds.uuid(15));
        verify(repository).deleteById(com.nick.job_application_tracker.TestIds.uuid(15));
    }
}
