package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.model.Location;

@Component
public class LocationMapper {

    public static LocationDTO toDTO(Location location) {
        if (location == null) return null;
        return new LocationDTO(location.getId(), location.getCity(), location.getCountry());
    }

    public static Location toEntity(LocationDTO dto) {
        if (dto == null) return null;
        Location location = new Location();
        location.setCity(dto.getCity());
        location.setCountry(dto.getCountry());
        return location;
    }

    public static void updateEntity(Location location, LocationDTO dto) {
        if (dto.getCity() != null) location.setCity(dto.getCity());
        if (dto.getCountry() != null) location.setCountry(dto.getCountry());
    }
}
