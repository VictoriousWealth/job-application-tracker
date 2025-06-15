package com.nick.job_application_tracker.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.service.LocationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/locations")
@Tag(name = "Locations", description = "Manage city/country information linked to job applications")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(summary = "Get all locations")
    @ApiResponse(responseCode = "200", description = "List of all locations",
        content = @Content(schema = @Schema(implementation = LocationDTO.class)))
    @GetMapping
    public List<LocationDTO> getAllLocations() {
        return locationService.getAllLocations();
    }

    @Operation(summary = "Get a location by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location found",
            content = @Content(schema = @Schema(implementation = LocationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new location")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location created successfully",
            content = @Content(schema = @Schema(implementation = LocationDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@RequestBody LocationDTO locationDTO) {
        return ResponseEntity.ok(locationService.createLocation(locationDTO));
    }

    @Operation(summary = "Update an existing location")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Location updated successfully",
            content = @Content(schema = @Schema(implementation = LocationDTO.class))),
        @ApiResponse(responseCode = "404", description = "Location not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<LocationDTO> updateLocation(@PathVariable Long id, @RequestBody LocationDTO locationDTO) {
        return locationService.updateLocation(id, locationDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a location by ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Location deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Location not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
