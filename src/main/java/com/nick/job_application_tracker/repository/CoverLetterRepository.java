package com.nick.job_application_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nick.job_application_tracker.model.CoverLetter;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {
}
