package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.custom.JobApplicationRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing and managing {@link JobApplication} entities.
 * Includes support for pagination, filtering, searching, and soft deletion.
 */
@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID>, JobApplicationRepositoryCustom {

    // --- Basic ownership + soft delete filtering ---

    Optional<JobApplication> findByIdAndUserIdAndDeletedFalse(UUID id, UUID userId);

    List<JobApplication> findByUserIdAndDeletedFalse(UUID userId);

    Page<JobApplication> findByUserIdAndDeletedFalse(UUID userId, Pageable pageable);

    // --- Status filters ---

    List<JobApplication> findByStatusAndDeletedFalse(JobApplication.Status status);

    Page<JobApplication> findByStatusAndDeletedFalse(JobApplication.Status status, Pageable pageable);

    Page<JobApplication> findByUserIdAndStatusAndDeletedFalse(UUID userId, JobApplication.Status status, Pageable pageable);

    long countByUserIdAndDeletedFalse(UUID userId);

    // --- Search (case-insensitive) ---

    Page<JobApplication> findByUserIdAndCompanyContainingIgnoreCaseAndDeletedFalse(UUID userId, String company, Pageable pageable);

    Page<JobApplication> findByUserIdAndJobTitleContainingIgnoreCaseAndDeletedFalse(UUID userId, String jobTitle, Pageable pageable);

    // --- Recent entries ---

    List<JobApplication> findTop5ByUserIdAndDeletedFalseOrderByCreatedAtDesc(UUID userId);

    // --- Time-based filtering (optional) ---

    List<JobApplication> findByUserIdAndAppliedOnBetweenAndDeletedFalse(UUID userId, LocalDateTime start, LocalDateTime end);

    // --- Optional soft-deleted viewing (e.g. recycle bin feature) ---

    Page<JobApplication> findByUserIdAndDeletedTrue(UUID userId, Pageable pageable);
}
