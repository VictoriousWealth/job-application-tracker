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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
