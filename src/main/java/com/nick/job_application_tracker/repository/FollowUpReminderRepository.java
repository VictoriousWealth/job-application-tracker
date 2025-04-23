package com.nick.job_application_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nick.job_application_tracker.model.FollowUpReminder;

@Repository
public interface FollowUpReminderRepository extends JpaRepository<FollowUpReminder, Long> {
    List<FollowUpReminder> findByJobApplicationId(Long jobApplicationId);
}
