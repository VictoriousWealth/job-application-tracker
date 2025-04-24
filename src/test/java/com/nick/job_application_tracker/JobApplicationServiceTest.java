package com.nick.job_application_tracker;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.nick.job_application_tracker.dto.JobApplicationDetailDTO;
import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.JobApplicationRepository;
import com.nick.job_application_tracker.service.JobApplicationService;

@ExtendWith(MockitoExtension.class)
class JobApplicationServiceTest {

    @Mock
    private JobApplicationRepository repository;

    @InjectMocks
    private JobApplicationService service;

    @Test
    void testGetByIdReturnsCorrectApplication() {
        JobApplication app = new JobApplication();
        app.setId(1L);
        app.setJobTitle("Engineer");

        when(repository.findById(1L)).thenReturn(Optional.of(app));

        JobApplicationDetailDTO result = service.getById(1L);
        assertEquals("Engineer", result.getJobTitle());
    }

    @Test
    void testDeleteCallsRepositoryDelete() {
        service.delete(5L);
        verify(repository).deleteById(5L);
    }
}
