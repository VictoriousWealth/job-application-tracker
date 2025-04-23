package com.nick.job_application_tracker.dto;

import jakarta.validation.constraints.NotBlank;

public class JobSourceCreateDTO {

    @NotBlank
    private String name;

    // Constructors
    public JobSourceCreateDTO() {}
    public JobSourceCreateDTO(String name) {
        this.name = name;
    }

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
