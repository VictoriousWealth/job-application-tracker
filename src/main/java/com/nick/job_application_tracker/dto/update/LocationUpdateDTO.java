package com.nick.job_application_tracker.dto.update;

import jakarta.validation.constraints.NotBlank;

public class LocationUpdateDTO {
    
    @NotBlank
    private String city;
    @NotBlank
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
