package com.nick.job_application_tracker.dto;

public class LocationDTO {
    public Long id;
    public String city;
    public String country;

    public LocationDTO(Long id, String city, String country) {
        this.id = id;
        this.city = city;
        this.country = country;
    }
}
