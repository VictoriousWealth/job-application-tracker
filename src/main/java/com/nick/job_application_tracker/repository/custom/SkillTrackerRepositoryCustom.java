package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.SkillTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SkillTrackerRepositoryCustom {

    Page<SkillTracker> searchBySkillName(UUID userId, String keyword, Pageable pageable);

    Page<SkillTracker> findRecentByUser(UUID userId, Pageable pageable);

    long countByUser(UUID userId);
}
