package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.Resume;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ResumeRepositoryCustom {

    Page<Resume> searchByFilePath(UUID userId, String keyword, Pageable pageable);

    Page<Resume> findRecent(UUID userId, LocalDateTime since, Pageable pageable);
}
