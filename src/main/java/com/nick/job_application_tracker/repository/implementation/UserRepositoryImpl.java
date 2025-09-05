package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.custom.UserRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> searchByEmail(String keyword, Pageable pageable) {
        String jpql = """
            SELECT u FROM User u
            WHERE u.deleted = false
              AND LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY u.email ASC
        """;

        List<User> results = entityManager.createQuery(jpql, User.class)
            .setParameter("keyword", keyword)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(u) FROM User u
            WHERE u.deleted = false
              AND LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """, Long.class)
            .setParameter("keyword", keyword)
            .getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public long countActiveUsers() {
        return entityManager.createQuery("""
            SELECT COUNT(u) FROM User u
            WHERE u.deleted = false
        """, Long.class).getSingleResult();
    }
}
