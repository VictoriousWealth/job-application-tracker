package com.nick.job_application_tracker.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.mapper.LocationMapper;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.LocationRepository;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final AuditLogService auditLogService;

    public LocationService(LocationRepository locationRepository, AuditLogService auditLogService) {
        this.locationRepository = locationRepository;
        this.auditLogService = auditLogService;
    }

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(LocationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<LocationDTO> getLocationById(Long id) {
        return locationRepository.findById(id).map(LocationMapper::toDTO);
    }

    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = LocationMapper.toEntity(locationDTO);
        Location savedLocation = locationRepository.save(location);
        
        auditLogService.logCreate("Created Location with ID " + savedLocation.getId() + " and city '" + savedLocation.getCity() + "'");
        
        return LocationMapper.toDTO(savedLocation);
    }

    public Optional<LocationDTO> updateLocation(Long id, LocationDTO locationDTO) {
        return locationRepository.findById(id).map(existing -> {
            LocationMapper.updateEntity(existing, locationDTO);
            Location updatedLocation = locationRepository.save(existing);
            
            auditLogService.logUpdate("Updated Location with ID " + updatedLocation.getId() + " to city '" + updatedLocation.getCity() + "'");
            
            return LocationMapper.toDTO(updatedLocation);
        });
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
        auditLogService.logDelete("Deleted Location with ID " + id);
    }
}
