package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.custom.CoverLetterRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, UUID>, CoverLetterRepositoryCustom {

    Page<CoverLetter> findByCreatedByIdAndDeletedFalse(UUID userId, Pageable pageable);

    Page<CoverLetter> findByCreatedByIdAndIsDraftTrueAndDeletedFalse(UUID userId, Pageable pageable);

    Page<CoverLetter> findByCreatedByIdAndDeletedTrue(UUID userId, Pageable pageable);

    long countByCreatedByIdAndDeletedFalse(UUID userId);
}
