package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.ApplicationTimeline;
import com.nick.job_application_tracker.repository.custom.ApplicationTimelineRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class ApplicationTimelineRepositoryImpl implements ApplicationTimelineRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ApplicationTimeline> findRecentEventsByUser(UUID userId, LocalDateTime since) {
        String jpql = """
            SELECT at
            FROM ApplicationTimeline at
            WHERE at.jobApplication.user.id = :userId
              AND at.deleted = false
              AND at.eventTime >= :since
              AND at.isDraft = false
            ORDER BY at.eventTime DESC
        """;

        TypedQuery<ApplicationTimeline> query = entityManager.createQuery(jpql, ApplicationTimeline.class)
                .setParameter("userId", userId)
                .setParameter("since", since);
        return query.getResultList();
    }

    @Override
    public long countByJobApplication(UUID jobAppId) {
        String jpql = """
            SELECT COUNT(at)
            FROM ApplicationTimeline at
            WHERE at.jobApplication.id = :jobAppId
              AND at.deleted = false
        """;

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("jobAppId", jobAppId)
                .getSingleResult();
    }

    @Override
    public long countFinalizedEventsByUser(UUID userId) {
        String jpql = """
            SELECT COUNT(at)
            FROM ApplicationTimeline at
            WHERE at.jobApplication.user.id = :userId
              AND at.deleted = false
              AND at.isDraft = false
        """;

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
