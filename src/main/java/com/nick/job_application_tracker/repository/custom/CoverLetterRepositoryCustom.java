package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.CoverLetter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface CoverLetterRepositoryCustom {
    Page<CoverLetter> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable);

    Page<CoverLetter> searchByTitle(UUID userId, String keyword, Pageable pageable);
}
