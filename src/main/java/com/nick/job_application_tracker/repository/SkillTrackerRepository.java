package com.nick.job_application_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nick.job_application_tracker.model.SkillTracker;

@Repository
public interface SkillTrackerRepository extends JpaRepository<SkillTracker, Long> {
    List<SkillTracker> findByJobApplicationId(Long jobApplicationId);
}
