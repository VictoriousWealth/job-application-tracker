package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.exception.client.ConflictException;
import com.nick.job_application_tracker.exception.client.NotFoundException;
import com.nick.job_application_tracker.mapper.LocationMapper;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.interfaces.LocationRepository;
import com.nick.job_application_tracker.service.interfaces.AuditLogService;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final AuditLogService auditLogService;

    public LocationService(LocationRepository locationRepository, AuditLogService auditLogService) {
        this.locationRepository = locationRepository;
        this.auditLogService = auditLogService;
    }

    public List<LocationResponseDTO> getAllLocations() {
        return locationRepository.findAllByDeletedFalse(Pageable.unpaged()).getContent().stream()
            .map(LocationMapper::toResponseDTO)
            .toList();
    }

    public Optional<LocationDetailDTO> getLocationById(UUID id) {
        return locationRepository.findByIdAndDeletedFalse(id).map(LocationMapper::toDetailDTO);
    }

    public LocationResponseDTO createLocation(LocationCreateDTO dto) {
        locationRepository.findByCityIgnoreCaseAndCountryIgnoreCaseAndDeletedFalse(dto.getCity(), dto.getCountry())
            .ifPresent(existing -> {
                throw new ConflictException("Location already exists");
            });
        Location saved = locationRepository.save(LocationMapper.toEntity(dto));
        auditLogService.logCreate("Created location with id: " + saved.getId());
        return LocationMapper.toResponseDTO(saved);
    }

    public Optional<LocationDetailDTO> updateLocation(UUID id, LocationUpdateDTO dto) {
        return locationRepository.findByIdAndDeletedFalse(id).map(existing -> {
            if (dto.getCity() != null && dto.getCountry() != null) {
                locationRepository.findByCityIgnoreCaseAndCountryIgnoreCaseAndDeletedFalse(dto.getCity(), dto.getCountry())
                    .filter(candidate -> !candidate.getId().equals(id))
                    .ifPresent(candidate -> {
                        throw new ConflictException("Location already exists");
                    });
            }
            LocationMapper.updateEntity(existing, dto);
            Location saved = locationRepository.save(existing);
            auditLogService.logUpdate("Updated location with id: " + saved.getId());
            return LocationMapper.toDetailDTO(saved);
        });
    }

    public void deleteLocation(UUID id) {
        Location location = getModelById(id);
        location.softDelete();
        locationRepository.save(location);
        auditLogService.logDelete("Deleted location with id: " + id);
    }

    public Location findOrCreate(String city, String country) {
        return locationRepository.findByCityIgnoreCaseAndCountryIgnoreCaseAndDeletedFalse(city, country)
            .orElseGet(() -> {
                Location location = new Location();
                location.setCity(city);
                location.setCountry(country);
                Location saved = locationRepository.save(location);
                auditLogService.logCreate("Created location with id: " + saved.getId());
                return saved;
            });
    }

    public LocationResponseDTO create(LocationCreateDTO dto) {
        return createLocation(dto);
    }

    public LocationDetailDTO getDetailById(UUID id) {
        return getLocationById(id)
            .orElseThrow(() -> new NotFoundException("Location not found", null));
    }

    public Location getModelById(UUID id) {
        return locationRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new NotFoundException("Location not found", null));
    }

    public Page<LocationResponseDTO> getAll(Pageable pageable) {
        return locationRepository.findAllByDeletedFalse(pageable).map(LocationMapper::toResponseDTO);
    }

    public LocationDetailDTO patchById(UUID id, JsonNode node) {
        Location location = getModelById(id);
        if (node.has("city")) {
            location.setCity(node.get("city").asText());
        }
        if (node.has("country")) {
            location.setCountry(node.get("country").asText());
        }
        locationRepository.save(location);
        auditLogService.logUpdate("Updated location with id: " + location.getId());
        return LocationMapper.toDetailDTO(location);
    }

    public LocationDetailDTO updateById(UUID id, LocationUpdateDTO dto) {
        Location location = getModelById(id);
        LocationMapper.updateEntity(location, dto);
        locationRepository.save(location);
        auditLogService.logUpdate("Updated location with id: " + location.getId());
        return LocationMapper.toDetailDTO(location);
    }

    public String deleteById(UUID id) {
        deleteLocation(id);
        return "No Content";
    }
}
