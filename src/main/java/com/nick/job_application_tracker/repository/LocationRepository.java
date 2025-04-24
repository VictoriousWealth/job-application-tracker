package com.nick.job_application_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nick.job_application_tracker.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByCityAndCountry(String city, String country);
}
