package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.JobSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobSourceRepositoryCustom {

    Page<JobSource> searchByName(String keyword, Pageable pageable);

    Page<JobSource> findRecent(Pageable pageable);
}
