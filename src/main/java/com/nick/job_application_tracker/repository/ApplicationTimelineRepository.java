package com.nick.job_application_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nick.job_application_tracker.model.ApplicationTimeline;

@Repository
public interface ApplicationTimelineRepository extends JpaRepository<ApplicationTimeline, Long> {
    List<ApplicationTimeline> findByJobApplicationId(Long jobApplicationId);
}
