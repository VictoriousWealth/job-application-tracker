package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.Resume;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    Optional<Resume> findByIdAndCreatedByAndDeletedFalse(UUID id, String createdBy);

    Page<Resume> findByCreatedByAndDeletedFalse(String createdBy, Pageable pageable);

    Page<Resume> findByCreatedByAndDeletedTrue(String createdBy, Pageable pageable);

    Page<Resume> findAllByDeletedFalse(Pageable pageable);

    Page<Resume> findAllByDeletedTrue(Pageable pageable);

    long countByCreatedByAndDeletedFalse(String createdBy);

    long countByDeletedFalse();

    @Modifying
    @Query("update Resume r set r.createdBy = :newCreatedBy where r.createdBy = :oldCreatedBy")
    int reassignOwnership(@Param("oldCreatedBy") String oldCreatedBy, @Param("newCreatedBy") String newCreatedBy);
}
