package com.nick.job_application_tracker.dto.update;

import jakarta.validation.constraints.NotBlank;

public class JobSourceUpdateDTO {

    @NotBlank
    private String name;

    // Getters and Setters

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }
    
}
