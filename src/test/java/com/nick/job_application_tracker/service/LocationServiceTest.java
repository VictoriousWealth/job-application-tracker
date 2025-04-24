package com.nick.job_application_tracker.service;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.model.Location;
import com.nick.job_application_tracker.repository.LocationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class LocationServiceTest {

    private LocationRepository locationRepository;
    private LocationService locationService;

    @BeforeEach
    void setup() {
        locationRepository = mock(LocationRepository.class);
        locationService = new LocationService(locationRepository);
    }

    @Test
    @DisplayName("Should return all locations")
    void testGetAllLocations() {
        Location l1 = new Location();
        l1.setId(1L);
        l1.setCity("London");
        l1.setCountry("UK");

        Location l2 = new Location();
        l2.setId(2L);
        l2.setCity("Paris");
        l2.setCountry("France");

        when(locationRepository.findAll()).thenReturn(List.of(l1, l2));

        List<LocationDTO> results = locationService.getAllLocations();

        assertThat(results).hasSize(2);
        assertThat(results).anyMatch(loc -> loc.getCity().equals("London"));
        assertThat(results).anyMatch(loc -> loc.getCountry().equals("France"));
    }

    @Test
    @DisplayName("Should return location by ID")
    void testGetLocationById() {
        Location location = new Location();
        location.setId(1L);
        location.setCity("Berlin");
        location.setCountry("Germany");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        Optional<LocationDTO> result = locationService.getLocationById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Berlin");
    }

    @Test
    @DisplayName("Should return empty when location not found")
    void testGetLocationByInvalidId() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<LocationDTO> result = locationService.getLocationById(99L);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should create a new location")
    void testCreateLocation() {
        LocationDTO dto = new LocationDTO(null, "Madrid", "Spain");

        Location saved = new Location();
        saved.setId(1L);
        saved.setCity("Madrid");
        saved.setCountry("Spain");

        when(locationRepository.save(any(Location.class))).thenReturn(saved);

        LocationDTO result = locationService.createLocation(dto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCity()).isEqualTo("Madrid");
    }

    @Test
    @DisplayName("Should update existing location")
    void testUpdateLocation() {
        Location existing = new Location();
        existing.setId(1L);
        existing.setCity("Old City");
        existing.setCountry("Old Country");

        LocationDTO updatedDTO = new LocationDTO(1L, "Rome", "Italy");

        when(locationRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(locationRepository.save(any(Location.class))).thenAnswer(i -> i.getArgument(0));

        Optional<LocationDTO> result = locationService.updateLocation(1L, updatedDTO);

        assertThat(result).isPresent();
        assertThat(result.get().getCity()).isEqualTo("Rome");
    }

    @Test
    @DisplayName("Should return empty when updating non-existing location")
    void testUpdateNonExistingLocation() {
        LocationDTO dto = new LocationDTO(null, "Nowhere", "Narnia");

        when(locationRepository.findById(100L)).thenReturn(Optional.empty());

        Optional<LocationDTO> result = locationService.updateLocation(100L, dto);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should delete location by ID")
    void testDeleteLocation() {
        locationService.deleteLocation(1L);
        verify(locationRepository).deleteById(1L);
    }
}
