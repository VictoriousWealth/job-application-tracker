package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.JobApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface JobApplicationRepositoryCustom {

    Page<JobApplication> findRecentApplicationsByUser(UUID userId, LocalDateTime since, Pageable pageable);

    long countByUserAndStatus(UUID userId, JobApplication.Status status);
}
