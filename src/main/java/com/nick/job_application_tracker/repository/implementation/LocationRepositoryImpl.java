package com.nick.job_application_tracker.repository.implementation;

import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.custom.LocationRepositoryCustom;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class LocationRepositoryImpl implements LocationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Location> searchByCityOrCountry(String keyword, Pageable pageable) {
        String jpql = """
            SELECT l FROM Location l
            WHERE l.deleted = false
              AND (
                  LOWER(l.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                  LOWER(l.country) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
        """;

        List<Location> results = entityManager.createQuery(jpql, Location.class)
                .setParameter("keyword", keyword)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(l) FROM Location l
            WHERE l.deleted = false
              AND (
                  LOWER(l.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                  LOWER(l.country) LIKE LOWER(CONCAT('%', :keyword, '%'))
              )
        """, Long.class).setParameter("keyword", keyword).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Location> findRecent(Pageable pageable) {
        String jpql = """
            SELECT l FROM Location l
            WHERE l.deleted = false
            ORDER BY l.createdAt DESC
        """;

        List<Location> results = entityManager.createQuery(jpql, Location.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long total = entityManager.createQuery("""
            SELECT COUNT(l) FROM Location l
            WHERE l.deleted = false
        """, Long.class).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
