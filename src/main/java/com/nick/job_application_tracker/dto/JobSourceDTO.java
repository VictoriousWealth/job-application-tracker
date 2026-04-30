package com.nick.job_application_tracker.dto;

import java.util.UUID;

public class JobSourceDTO {
    private UUID id;
    private String name;

    public JobSourceDTO() {}

    public JobSourceDTO(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public JobSourceDTO(Long id, String name) {
        this(LegacyIdAdapter.fromLong(id), name);
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
