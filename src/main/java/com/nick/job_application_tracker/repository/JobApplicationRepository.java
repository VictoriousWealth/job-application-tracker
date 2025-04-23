package com.nick.job_application_tracker.repository;

import com.nick.job_application_tracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByUserId(Long userId);
    List<JobApplication> findByStatus(JobApplication.Status status);
}
