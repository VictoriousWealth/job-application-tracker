package com.nick.job_application_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nick.job_application_tracker.model.Resume;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
}
