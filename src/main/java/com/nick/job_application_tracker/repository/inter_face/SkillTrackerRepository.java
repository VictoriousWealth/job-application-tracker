package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.dto.LegacyIdAdapter;
import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.custom.SkillTrackerRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SkillTrackerRepository extends JpaRepository<SkillTracker, UUID>, SkillTrackerRepositoryCustom {

    Page<SkillTracker> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default java.util.List<SkillTracker> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    default java.util.List<SkillTracker> findByJobApplicationId(Long jobApplicationId) {
        return findByJobApplicationId(LegacyIdAdapter.fromLong(jobApplicationId));
    }

    Page<SkillTracker> findAllByDeletedFalse(Pageable pageable);

    Page<SkillTracker> findAllByDeletedTrue(Pageable pageable);

    long countByDeletedFalse();
}
