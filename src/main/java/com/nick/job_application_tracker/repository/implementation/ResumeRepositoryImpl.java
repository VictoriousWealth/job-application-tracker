package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.Resume;
import com.nick.job_application_tracker.repository.custom.ResumeRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ResumeRepositoryImpl implements ResumeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Resume> searchByFilePath(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT r FROM Resume r
            WHERE r.deleted = false
              AND r.createdBy = :createdBy
              AND LOWER(r.filePath) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """;

        List<Resume> results = entityManager.createQuery(jpql, Resume.class)
                .setParameter("createdBy", userId.toString())
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(r) FROM Resume r
            WHERE r.deleted = false
              AND r.createdBy = :createdBy
              AND LOWER(r.filePath) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, Long.class)
                .setParameter("createdBy", userId.toString())
                .setParameter("keyword", keyword)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Resume> findRecent(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT r FROM Resume r
            WHERE r.deleted = false
              AND r.createdBy = :createdBy
              AND r.createdAt >= :since
            ORDER BY r.createdAt DESC
        """;

        List<Resume> results = entityManager.createQuery(jpql, Resume.class)
                .setParameter("createdBy", userId.toString())
                .setParameter("since", since)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(r) FROM Resume r
            WHERE r.deleted = false
              AND r.createdBy = :createdBy
              AND r.createdAt >= :since
        """, Long.class)
                .setParameter("createdBy", userId.toString())
                .setParameter("since", since)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
