package com.nick.job_application_tracker.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.mapper.LocationMapper;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository repo;
    private final LocationMapper mapper;
    private final AuditLogService auditLogService;

    public LocationService(LocationRepository repo, LocationMapper mapper, AuditLogService auditLogService) {
        this.repo = repo;
        this.mapper = mapper;
        this.auditLogService = auditLogService;
    }

    public List<LocationDTO> findAll() {
        return repo.findAll().stream()
            .map(mapper::toDTO)
            .toList();
    }

    public LocationDTO createLocation(LocationDTO dto) {
        Location location = mapper.toEntity(dto);
        Location saved = repo.save(location);

        auditLogService.logCreate("Created location: " + saved.getCity() + ", " + saved.getCountry());
        return mapper.toDTO(saved);
    }

    public LocationDTO updateLocation(Long id, LocationDTO dto) {
        Location existing = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));

        existing.setCity(dto.getCity());
        existing.setCountry(dto.getCountry());
        Location updated = repo.save(existing);

        auditLogService.logUpdate("Updated location ID: " + id);
        return mapper.toDTO(updated);
    }

    public void deleteLocation(Long id) {
        Location existing = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));

        repo.delete(existing);
        auditLogService.logDelete("Deleted location ID: " + id);
    }
}
