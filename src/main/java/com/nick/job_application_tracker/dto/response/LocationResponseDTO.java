package com.nick.job_application_tracker.dto.response;

import java.util.UUID;

import com.nick.job_application_tracker.model.Location;

/**
 * {@link Location}
 */
public class LocationResponseDTO {
    private String city;
    private String country;

    // Getters and Setters
    
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