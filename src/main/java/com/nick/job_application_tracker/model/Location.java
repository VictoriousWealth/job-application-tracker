package com.nick.job_application_tracker.model;

import com.nick.job_application_tracker.model.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Represents a geographical location (city and country) for job applications.
 */
@Entity
@Table(name = "location")
public class Location extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String city;

    @NotBlank
    @Column(nullable = false)
    private String country;

    // --- Constructors ---

    public Location() {}

    public Location(String city, String country) {
        this.city = city;
        this.country = country;
    }

    // --- Lifecycle Hooks ---

    @PrePersist
    public void prePersist() {
        // Normalize formatting
        city = capitalize(city.trim());
        country = capitalize(country.trim());
    }

    // Capitalize first letter of each word
    private String capitalize(String input) {
        if (input.isBlank()) return input;
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isBlank()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }

    // --- Getters and Setters ---

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
