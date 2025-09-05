package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.ScheduledCommunication;
import com.nick.job_application_tracker.repository.custom.ScheduledCommunicationRepositoryCustom;

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
public class ScheduledCommunicationRepositoryImpl implements ScheduledCommunicationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ScheduledCommunication> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT s FROM ScheduledCommunication s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
              AND s.scheduledFor >= :since
            ORDER BY s.scheduledFor DESC
        """;

        List<ScheduledCommunication> results = entityManager.createQuery(jpql, ScheduledCommunication.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(s) FROM ScheduledCommunication s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
              AND s.scheduledFor >= :since
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long countByUserAndType(UUID userId, ScheduledCommunication.Type type) {
        String jpql = """
            SELECT COUNT(s) FROM ScheduledCommunication s
            WHERE s.jobApplication.user.id = :userId
              AND s.type = :type
              AND s.deleted = false
        """;

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("type", type)
                .getSingleResult();
    }

    @Override
    public Page<ScheduledCommunication> searchByNotes(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT s FROM ScheduledCommunication s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
              AND LOWER(s.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """;

        List<ScheduledCommunication> results = entityManager.createQuery(jpql, ScheduledCommunication.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(s) FROM ScheduledCommunication s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
              AND LOWER(s.notes) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
