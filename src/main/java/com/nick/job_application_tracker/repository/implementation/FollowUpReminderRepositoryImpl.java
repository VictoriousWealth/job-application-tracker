package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.FollowUpReminder;
import com.nick.job_application_tracker.repository.custom.FollowUpReminderRepositoryCustom;

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
public class FollowUpReminderRepositoryImpl implements FollowUpReminderRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<FollowUpReminder> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT r FROM FollowUpReminder r
            WHERE r.jobApplication.user.id = :userId
              AND r.deleted = false
              AND r.createdAt >= :since
            ORDER BY r.createdAt DESC
        """;

        List<FollowUpReminder> results = entityManager.createQuery(jpql, FollowUpReminder.class)
            .setParameter("userId", userId)
            .setParameter("since", since)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(r) FROM FollowUpReminder r
            WHERE r.jobApplication.user.id = :userId
              AND r.deleted = false
              AND r.createdAt >= :since
        """, Long.class)
            .setParameter("userId", userId)
            .setParameter("since", since)
            .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<FollowUpReminder> searchByNote(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT r FROM FollowUpReminder r
            WHERE r.jobApplication.user.id = :userId
              AND r.deleted = false
              AND LOWER(r.note) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY r.createdAt DESC
        """;

        List<FollowUpReminder> results = entityManager.createQuery(jpql, FollowUpReminder.class)
            .setParameter("userId", userId)
            .setParameter("keyword", keyword)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(r) FROM FollowUpReminder r
            WHERE r.jobApplication.user.id = :userId
              AND r.deleted = false
              AND LOWER(r.note) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, Long.class)
            .setParameter("userId", userId)
            .setParameter("keyword", keyword)
            .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
