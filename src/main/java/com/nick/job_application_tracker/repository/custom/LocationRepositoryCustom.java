package com.nick.job_application_tracker.repository.custom;

import com.nick.job_application_tracker.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationRepositoryCustom {
    Page<Location> searchByCityOrCountry(String keyword, Pageable pageable);
    Page<Location> findRecent(Pageable pageable);
}
