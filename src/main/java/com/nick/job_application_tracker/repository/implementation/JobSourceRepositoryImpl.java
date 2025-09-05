package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.JobSource;
import com.nick.job_application_tracker.repository.custom.JobSourceRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class JobSourceRepositoryImpl implements JobSourceRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<JobSource> searchByName(String keyword, Pageable pageable) {
        String jpql = """
            SELECT s FROM JobSource s
            WHERE s.deleted = false
              AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY s.name ASC
        """;

        List<JobSource> results = entityManager.createQuery(jpql, JobSource.class)
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(s) FROM JobSource s
            WHERE s.deleted = false
              AND LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, Long.class)
                .setParameter("keyword", keyword)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<JobSource> findRecent(Pageable pageable) {
        String jpql = """
            SELECT s FROM JobSource s
            WHERE s.deleted = false
            ORDER BY s.createdAt DESC
        """;

        List<JobSource> results = entityManager.createQuery(jpql, JobSource.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(s) FROM JobSource s
            WHERE s.deleted = false
        """, Long.class).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
