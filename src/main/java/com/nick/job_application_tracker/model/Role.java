package com.nick.job_application_tracker.model;

public enum Role {
    BASIC,
    ADMIN;

    public static Role from(String value) {
        return Role.valueOf(value.toUpperCase());
    }

    public String getName() {
        return name();
    }
}
