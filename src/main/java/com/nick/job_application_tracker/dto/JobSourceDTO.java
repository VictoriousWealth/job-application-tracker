package com.nick.job_application_tracker.dto;

public class JobSourceDTO {
    private Long id;
    private String name;

    // Constructors
    public JobSourceDTO() {}
    public JobSourceDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
