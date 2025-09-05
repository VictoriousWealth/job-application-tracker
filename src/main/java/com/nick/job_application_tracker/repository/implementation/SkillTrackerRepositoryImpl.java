package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.SkillTracker;
import com.nick.job_application_tracker.repository.custom.SkillTrackerRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class SkillTrackerRepositoryImpl implements SkillTrackerRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<SkillTracker> searchBySkillName(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT s FROM SkillTracker s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
              AND LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """;

        List<SkillTracker> results = entityManager.createQuery(jpql, SkillTracker.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(s) FROM SkillTracker s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
              AND LOWER(s.skillName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("keyword", keyword)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<SkillTracker> findRecentByUser(UUID userId, Pageable pageable) {
        String jpql = """
            SELECT s FROM SkillTracker s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
            ORDER BY s.createdAt DESC
        """;

        List<SkillTracker> results = entityManager.createQuery(jpql, SkillTracker.class)
                .setParameter("userId", userId)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(s) FROM SkillTracker s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
        """, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long countByUser(UUID userId) {
        String jpql = """
            SELECT COUNT(s) FROM SkillTracker s
            WHERE s.jobApplication.user.id = :userId
              AND s.deleted = false
        """;

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
    }
}
