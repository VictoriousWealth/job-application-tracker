package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.CommunicationLog;
import com.nick.job_application_tracker.repository.custom.CommunicationLogRepositoryCustom;

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
public class CommunicationLogRepositoryImpl implements CommunicationLogRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CommunicationLog> findRecentLogsByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT cl
            FROM CommunicationLog cl
            WHERE cl.jobApplication.user.id = :userId
              AND cl.deleted = false
              AND cl.timestamp >= :since
            ORDER BY cl.timestamp DESC
        """;

        List<CommunicationLog> results = entityManager.createQuery(jpql, CommunicationLog.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(cl)
            FROM CommunicationLog cl
            WHERE cl.jobApplication.user.id = :userId
              AND cl.deleted = false
              AND cl.timestamp >= :since
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long countByUserAndMethod(UUID userId, CommunicationLog.Method method) {
        String jpql = """
            SELECT COUNT(cl)
            FROM CommunicationLog cl
            WHERE cl.jobApplication.user.id = :userId
              AND cl.type = :method
              AND cl.deleted = false
        """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("method", method)
                .getSingleResult();
    }

    @Override
    public Page<CommunicationLog> searchByMessage(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT cl
            FROM CommunicationLog cl
            WHERE cl.jobApplication.user.id = :userId
              AND LOWER(cl.message) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND cl.deleted = false
            ORDER BY cl.timestamp DESC
        """;

        List<CommunicationLog> logs = entityManager.createQuery(jpql, CommunicationLog.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(cl)
            FROM CommunicationLog cl
            WHERE cl.jobApplication.user.id = :userId
              AND LOWER(cl.message) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND cl.deleted = false
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .getSingleResult();

        return new PageImpl<>(logs, pageable, total);
    }
}
