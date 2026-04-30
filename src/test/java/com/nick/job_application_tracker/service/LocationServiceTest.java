package com.nick.job_application_tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.inter_face.LocationRepository;
import com.nick.job_application_tracker.service.implementation.LocationService;
import com.nick.job_application_tracker.service.inter_face.AuditLogService;

public class LocationServiceTest {

    private static final UUID LOCATION_ID = UUID.fromString("00000000-0000-0000-0000-000000000401");

    private LocationRepository locationRepository;
    private LocationService locationService;
    private AuditLogService auditLogService;

    @BeforeEach
    void setup() {
        locationRepository = mock(LocationRepository.class);
        auditLogService = mock(AuditLogService.class);
        locationService = new LocationService(locationRepository, auditLogService);
    }

    @Test
    @DisplayName("Should return all locations")
    void testGetAllLocations() {
        Location location = new Location();
        location.setId(LOCATION_ID);
        location.setCity("London");
        location.setCountry("UK");

        when(locationRepository.findAll()).thenReturn(List.of(location));

        List<LocationResponseDTO> results = locationService.getAllLocations();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getId()).isEqualTo(LOCATION_ID);
        assertThat(results.get(0).getCity()).isEqualTo("London");
    }

    @Test
    @DisplayName("Should return location by ID")
    void testGetLocationById() {
        Location location = new Location();
        location.setId(LOCATION_ID);
        location.setCity("Berlin");
        location.setCountry("Germany");

        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(location));

        Optional<LocationDetailDTO> result = locationService.getLocationById(LOCATION_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Berlin");
    }

    @Test
    @DisplayName("Should create a new location")
    void testCreateLocation() {
        LocationCreateDTO dto = new LocationCreateDTO();
        dto.setCity("Madrid");
        dto.setCountry("Spain");

        Location saved = new Location();
        saved.setId(LOCATION_ID);
        saved.setCity("Madrid");
        saved.setCountry("Spain");

        when(locationRepository.save(any(Location.class))).thenReturn(saved);

        LocationResponseDTO result = locationService.createLocation(dto);

        assertThat(result.getId()).isEqualTo(LOCATION_ID);
        assertThat(result.getCity()).isEqualTo("Madrid");
    }

    @Test
    @DisplayName("Should update existing location")
    void testUpdateLocation() {
        Location existing = new Location();
        existing.setId(LOCATION_ID);
        existing.setCity("Old City");
        existing.setCountry("Old Country");

        LocationUpdateDTO updateDTO = new LocationUpdateDTO();
        updateDTO.setCity("Rome");
        updateDTO.setCountry("Italy");

        when(locationRepository.findById(LOCATION_ID)).thenReturn(Optional.of(existing));
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<LocationDetailDTO> result = locationService.updateLocation(LOCATION_ID, updateDTO);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Rome");
    }

    @Test
    @DisplayName("Should delete location by ID")
    void testDeleteLocation() {
        locationService.deleteLocation(LOCATION_ID);
        verify(locationRepository).deleteById(LOCATION_ID);
    }
}
