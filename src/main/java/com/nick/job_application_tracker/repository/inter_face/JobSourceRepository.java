package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.custom.JobSourceRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobSourceRepository extends JpaRepository<JobSource, UUID>, JobSourceRepositoryCustom {

    Optional<JobSource> findByNameIgnoreCaseAndDeletedFalse(String name);

    Page<JobSource> findAllByDeletedFalse(Pageable pageable);

    Page<JobSource> findAllByDeletedTrue(Pageable pageable);

    long countByDeletedFalse();
}
