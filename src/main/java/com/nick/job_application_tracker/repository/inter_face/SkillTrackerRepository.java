package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.model.SkillTracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SkillTrackerRepository extends JpaRepository<SkillTracker, UUID> {

    Page<SkillTracker> findByJobApplicationIdAndDeletedFalse(UUID jobApplicationId, Pageable pageable);

    default java.util.List<SkillTracker> findByJobApplicationId(UUID jobApplicationId) {
        return findByJobApplicationIdAndDeletedFalse(jobApplicationId, Pageable.unpaged()).getContent();
    }

    Page<SkillTracker> findAllByDeletedFalse(Pageable pageable);

    Page<SkillTracker> findAllByDeletedTrue(Pageable pageable);

    long countByDeletedFalse();
}
