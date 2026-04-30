package com.nick.job_application_tracker.mapper;

import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.dto.LocationDTO;
import com.nick.job_application_tracker.dto.create.LocationCreateDTO;
import com.nick.job_application_tracker.dto.detail.LocationDetailDTO;
import com.nick.job_application_tracker.dto.response.LocationResponseDTO;
import com.nick.job_application_tracker.dto.update.LocationUpdateDTO;
import com.nick.job_application_tracker.model.Location;

@Component
public class LocationMapper {

    public static LocationResponseDTO toResponseDTO(Location location) {
        if (location == null) {
            return null;
        }
        LocationResponseDTO dto = new LocationResponseDTO();
        dto.setId(location.getId());
        dto.setCity(location.getCity());
        dto.setCountry(location.getCountry());
        return dto;
    }

    public static LocationDetailDTO toDetailDTO(Location location) {
        if (location == null) {
            return null;
        }
        LocationDetailDTO dto = new LocationDetailDTO();
        dto.setId(location.getId());
        dto.setCity(location.getCity());
        dto.setCountry(location.getCountry());
        return dto;
    }

    public static LocationDTO toDTO(Location location) {
        if (location == null) return null;
        return new LocationDTO(location.getId(), location.getCity(), location.getCountry());
    }

    public static Location toEntity(LocationCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        Location location = new Location();
        location.setCity(dto.getCity());
        location.setCountry(dto.getCountry());
        return location;
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

    public static void updateEntity(Location location, LocationUpdateDTO dto) {
        if (dto.getCity() != null) {
            location.setCity(dto.getCity());
        }
        if (dto.getCountry() != null) {
            location.setCountry(dto.getCountry());
        }
    }
}
