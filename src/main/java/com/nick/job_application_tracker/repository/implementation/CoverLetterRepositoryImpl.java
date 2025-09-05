package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.CoverLetter;
import com.nick.job_application_tracker.repository.custom.CoverLetterRepositoryCustom;

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
public class CoverLetterRepositoryImpl implements CoverLetterRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<CoverLetter> findRecentByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT c FROM CoverLetter c
            WHERE c.createdBy.id = :userId
              AND c.deleted = false
              AND c.createdAt >= :since
            ORDER BY c.createdAt DESC
        """;

        List<CoverLetter> results = entityManager.createQuery(jpql, CoverLetter.class)
            .setParameter("userId", userId)
            .setParameter("since", since)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(c)
            FROM CoverLetter c
            WHERE c.createdBy.id = :userId
              AND c.deleted = false
              AND c.createdAt >= :since
        """, Long.class)
            .setParameter("userId", userId)
            .setParameter("since", since)
            .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<CoverLetter> searchByTitle(UUID userId, String keyword, Pageable pageable) {
        String jpql = """
            SELECT c FROM CoverLetter c
            WHERE c.createdBy.id = :userId
              AND LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND c.deleted = false
            ORDER BY c.createdAt DESC
        """;

        List<CoverLetter> results = entityManager.createQuery(jpql, CoverLetter.class)
            .setParameter("userId", userId)
            .setParameter("keyword", keyword)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(c)
            FROM CoverLetter c
            WHERE c.createdBy.id = :userId
              AND LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              AND c.deleted = false
        """, Long.class)
            .setParameter("userId", userId)
            .setParameter("keyword", keyword)
            .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
