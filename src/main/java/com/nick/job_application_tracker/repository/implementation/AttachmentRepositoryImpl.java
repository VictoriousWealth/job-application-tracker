package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.Attachment;
import com.nick.job_application_tracker.repository.custom.AttachmentRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class AttachmentRepositoryImpl implements AttachmentRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Attachment> findRecentAttachmentsByUser(UUID userId, LocalDateTime since, Pageable pageable) {
        String jpql = """
            SELECT a FROM Attachment a
            WHERE a.jobApplication.user.id = :userId
              AND a.deleted = false
              AND a.createdAt >= :since
            ORDER BY a.createdAt DESC
        """;

        TypedQuery<Attachment> query = entityManager.createQuery(jpql, Attachment.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        List<Attachment> attachments = query.getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(a)
            FROM Attachment a
            WHERE a.jobApplication.user.id = :userId
              AND a.deleted = false
              AND a.createdAt >= :since
        """, Long.class)
                .setParameter("userId", userId)
                .setParameter("since", since)
                .getSingleResult();

        return new PageImpl<>(attachments, pageable, total);
    }

    @Override
    public long countByUserAndType(UUID userId, Attachment.Type type) {
        String jpql = """
            SELECT COUNT(a)
            FROM Attachment a
            WHERE a.jobApplication.user.id = :userId
              AND a.type = :type
              AND a.deleted = false
        """;
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .setParameter("type", type)
                .getSingleResult();
    }
}
