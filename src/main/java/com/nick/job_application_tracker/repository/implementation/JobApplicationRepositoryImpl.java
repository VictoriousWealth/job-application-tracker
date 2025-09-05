package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.JobApplication;
import com.nick.job_application_tracker.repository.custom.JobApplicationRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JobApplicationRepositoryImpl implements JobApplicationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<JobApplication> findRecentApplicationsByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT ja
            FROM JobApplication ja
            WHERE ja.user.id = :userId
              AND ja.deleted = false
              AND ja.createdAt >= :since
            ORDER BY ja.createdAt DESC
        """;

        List<JobApplication> results = entityManager.createQuery(jpql, JobApplication.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(ja)
            FROM JobApplication ja
            WHERE ja.user.id = :userId
              AND ja.deleted = false
              AND ja.createdAt >= :since
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long countByUserAndStatus(UUID userId, JobApplication.Status status) {
        String jpql = """
            SELECT COUNT(ja)
            FROM JobApplication ja
            WHERE ja.user.id = :userId
              AND ja.status = :status
              AND ja.deleted = false
        """;

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("status", status)
                .getSingleResult();
    }
}
