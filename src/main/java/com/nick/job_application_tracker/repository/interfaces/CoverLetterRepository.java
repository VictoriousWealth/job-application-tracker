package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.CoverLetter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, UUID> {

    Optional<CoverLetter> findByIdAndCreatedByAndDeletedFalse(UUID id, String createdBy);

    Page<CoverLetter> findByCreatedByAndDeletedFalse(String createdBy, Pageable pageable);

    Page<CoverLetter> findByCreatedByAndIsDraftTrueAndDeletedFalse(String createdBy, Pageable pageable);

    Page<CoverLetter> findByCreatedByAndDeletedTrue(String createdBy, Pageable pageable);

    long countByCreatedByAndDeletedFalse(String createdBy);
}
