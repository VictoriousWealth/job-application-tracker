package com.nick.job_application_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nick.job_application_tracker.model.ScheduledCommunication;

@Repository
public interface ScheduledCommunicationRepository extends JpaRepository<ScheduledCommunication, Long> {
    List<ScheduledCommunication> findByJobApplicationId(Long jobApplicationId);
}
