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

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
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
        return LocationMapper.toDTO(locationRepository.save(location));
    }

    public Optional<LocationDTO> updateLocation(Long id, LocationDTO locationDTO) {
        return locationRepository.findById(id).map(existing -> {
            LocationMapper.updateEntity(existing, locationDTO);
            return LocationMapper.toDTO(locationRepository.save(existing));
        });
    }

    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
