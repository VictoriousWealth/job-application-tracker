package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class LocationDTO {
    private UUID id;
    private String city;
    private String country;

    public LocationDTO() {}

    public LocationDTO(UUID id, String city, String country) {
        this.id = id;
        this.city = city;
        this.country = country;
    }

    public LocationDTO(Long id, String city, String country) {
        this(LegacyIdAdapter.fromLong(id), city, country);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = LegacyIdAdapter.fromLong(id);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
