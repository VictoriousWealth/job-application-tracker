package com.nick.job_application_tracker.repository.inter_face;

import com.nick.job_application_tracker.dto.LegacyIdAdapter;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.custom.LocationRepositoryCustom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID>, LocationRepositoryCustom {
    default Optional<Location> findById(Long id) {
        return findById(LegacyIdAdapter.fromLong(id));
    }

    default void deleteById(Long id) {
        deleteById(LegacyIdAdapter.fromLong(id));
    }

    Optional<Location> findByCityAndCountry(String city, String country);
    Page<Location> findAllByDeletedFalse(Pageable pageable);
}
