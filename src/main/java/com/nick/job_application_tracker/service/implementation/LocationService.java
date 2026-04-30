package com.nick.job_application_tracker.service.implementation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.exception.client_exception.NotFoundException;
import com.nick.job_application_tracker.mapper.LocationMapper;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.inter_face.LocationRepository;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;
import com.nick.job_application_tracker.service.specialised_common.LocationServiceInterface;

@Service
public class LocationService implements LocationServiceInterface {

    private final LocationRepository locationRepository;
    private final AuditLogService auditLogService;

    public LocationService(LocationRepository locationRepository, AuditLogService auditLogService) {
        this.locationRepository = locationRepository;
        this.auditLogService = auditLogService;
    }

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(LocationMapper::toDTO)
            .toList();
    }

    public Optional<LocationDTO> getLocationById(UUID id) {
        return locationRepository.findById(id).map(LocationMapper::toDTO);
    }

    public Optional<LocationDTO> getLocationById(Long id) {
        return getLocationById(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id));
    }

    public LocationDTO createLocation(LocationDTO dto) {
        Location saved = locationRepository.save(LocationMapper.toEntity(dto));
        auditLogService.logCreate("Created location with id: " + saved.getId());
        return LocationMapper.toDTO(saved);
    }

    public Optional<LocationDTO> updateLocation(UUID id, LocationDTO dto) {
        return locationRepository.findById(id).map(existing -> {
            LocationMapper.updateEntity(existing, dto);
            Location saved = locationRepository.save(existing);
            auditLogService.logUpdate("Updated location with id: " + saved.getId());
            return LocationMapper.toDTO(saved);
        });
    }

    public Optional<LocationDTO> updateLocation(Long id, LocationDTO dto) {
        return updateLocation(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id), dto);
    }

    public void deleteLocation(UUID id) {
        locationRepository.deleteById(id);
        auditLogService.logDelete("Deleted location with id: " + id);
    }

    public void deleteLocation(Long id) {
        deleteLocation(com.nick.job_application_tracker.dto.LegacyIdAdapter.fromLong(id));
    }

    @Override
    public LocationResponseDTO create(LocationCreateDTO dto) {
        Location location = new Location();
        location.setCity(dto.getCity());
        location.setCountry(dto.getCountry());
        Location saved = locationRepository.save(location);

        LocationResponseDTO response = new LocationResponseDTO();
        response.setCity(saved.getCity());
        response.setCountry(saved.getCountry());
        return response;
    }

    @Override
    public LocationDetailDTO getDetailById(UUID id) {
        Location location = getModelById(id);
        LocationDetailDTO dto = new LocationDetailDTO();
        dto.setId(location.getId());
        dto.setCity(location.getCity());
        dto.setCountry(location.getCountry());
        return dto;
    }

    @Override
    public Location getModelById(UUID id) {
        return locationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Location not found", null));
    }

    @Override
    public Page<LocationResponseDTO> getAll(Pageable pageable) {
        return locationRepository.findAllByDeletedFalse(pageable).map(location -> {
            LocationResponseDTO dto = new LocationResponseDTO();
            dto.setCity(location.getCity());
            dto.setCountry(location.getCountry());
            return dto;
        });
    }

    @Override
    public LocationDetailDTO patchById(UUID id, JsonNode node) {
        Location location = getModelById(id);
        if (node.has("city")) {
            location.setCity(node.get("city").asText());
        }
        if (node.has("country")) {
            location.setCountry(node.get("country").asText());
        }
        locationRepository.save(location);
        return getDetailById(id);
    }

    @Override
    public LocationDetailDTO updateById(UUID id, LocationUpdateDTO dto) {
        Location location = getModelById(id);
        location.setCity(dto.getCity());
        location.setCountry(dto.getCountry());
        locationRepository.save(location);
        return getDetailById(id);
    }

    @Override
    public String deleteById(UUID id) {
        deleteLocation(id);
        return "No Content";
    }
}
