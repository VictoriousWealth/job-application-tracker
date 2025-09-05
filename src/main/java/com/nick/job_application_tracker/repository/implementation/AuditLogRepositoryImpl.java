package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.AuditLog;
import com.nick.job_application_tracker.repository.custom.AuditLogRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<AuditLog> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT a FROM AuditLog a
            WHERE a.performedBy.id = :userId
              AND a.deleted = false
              AND a.createdAt >= :since
            ORDER BY a.createdAt DESC
        """;

        List<AuditLog> results = entityManager.createQuery(jpql, AuditLog.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.performedBy.id = :userId
              AND a.deleted = false
              AND a.createdAt >= :since
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long countByUserAndAction(UUID userId, AuditLog.Action action) {
        String jpql = """
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.performedBy.id = :userId
              AND a.action = :action
              AND a.deleted = false
        """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("action", action)
                .getSingleResult();
    }

    @Override
    public Page<AuditLog> searchByDescription(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT a FROM AuditLog a
            WHERE a.performedBy.id = :userId
              AND LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND a.deleted = false
            ORDER BY a.createdAt DESC
        """;

        List<AuditLog> logs = entityManager.createQuery(jpql, AuditLog.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(a) FROM AuditLog a
            WHERE a.performedBy.id = :userId
              AND LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND a.deleted = false
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .getSingleResult();

        return new PageImpl<>(logs, pageable, total);
    }
}
