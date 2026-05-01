package com.nick.job_application_tracker.repository.interfaces;

import com.nick.job_application_tracker.model.Location;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    Optional<Location> findByCityAndCountry(String city, String country);
    Optional<Location> findByCityIgnoreCaseAndCountryIgnoreCaseAndDeletedFalse(String city, String country);
    Optional<Location> findByIdAndDeletedFalse(UUID id);
    Page<Location> findAllByDeletedFalse(Pageable pageable);
}
