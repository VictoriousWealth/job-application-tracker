package com.nick.job_application_tracker.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.service.implementation.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Locations", description = "Manage city/country information linked to job applications")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Get all locations")
    @GetMapping
    public ResponseEntity<List<LocationResponseDTO>> getAll() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @Operation(summary = "Get a location by ID")
    @GetMapping("/{id}")
    public ResponseEntity<LocationDetailDTO> getById(@PathVariable UUID id) {
        return locationService.getLocationById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a location")
    @PostMapping
    public ResponseEntity<LocationResponseDTO> create(@Valid @RequestBody LocationCreateDTO dto) {
        return ResponseEntity.ok(locationService.createLocation(dto));
    }

    @Operation(summary = "Update a location")
    @PutMapping("/{id}")
    public ResponseEntity<LocationDetailDTO> update(@PathVariable UUID id, @Valid @RequestBody LocationUpdateDTO dto) {
        return locationService.updateLocation(id, dto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a location")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
