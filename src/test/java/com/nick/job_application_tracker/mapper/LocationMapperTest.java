package com.nick.job_application_tracker.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.model.Location;

public class LocationMapperTest {

    @Test
    @DisplayName("Should map Location entity to DTO")
    void testToDTO() {
        Location location = new Location();
        location.setId(10L);
        location.setCity("Paris");
        location.setCountry("France");

        LocationDTO dto = LocationMapper.toDTO(location);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getCity()).isEqualTo("Paris");
        assertThat(dto.getCountry()).isEqualTo("France");
    }

    @Test
    @DisplayName("Should return null when mapping null Location to DTO")
    void testToDTONull() {
        LocationDTO dto = LocationMapper.toDTO(null);
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should map LocationDTO to entity")
    void testToEntity() {
        LocationDTO dto = new LocationDTO();
        dto.setCity("London");
        dto.setCountry("UK");

        Location entity = LocationMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getCity()).isEqualTo("London");
        assertThat(entity.getCountry()).isEqualTo("UK");
    }

    @Test
    @DisplayName("Should return null when mapping null DTO to Location")
    void testToEntityNull() {
        Location entity = LocationMapper.toEntity(null);
        assertThat(entity).isNull();
    }

    @Test
    @DisplayName("Should update entity fields from DTO if not null")
    void testUpdateEntity() {
        Location location = new Location();
        location.setCity("Old City");
        location.setCountry("Old Country");

        LocationDTO dto = new LocationDTO();
        dto.setCity("New York");
        dto.setCountry("USA");

        LocationMapper.updateEntity(location, dto);

        assertThat(location.getCity()).isEqualTo("New York");
        assertThat(location.getCountry()).isEqualTo("USA");
    }

    @Test
    @DisplayName("Should not update fields in entity if DTO fields are null")
    void testUpdateEntityWithNulls() {
        Location location = new Location();
        location.setCity("Original City");
        location.setCountry("Original Country");

        LocationDTO dto = new LocationDTO(); // all fields are null by default

        LocationMapper.updateEntity(location, dto);

        assertThat(location.getCity()).isEqualTo("Original City");
        assertThat(location.getCountry()).isEqualTo("Original Country");
    }
}
