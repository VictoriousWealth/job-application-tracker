package com.nick.job_application_tracker.model;

public enum Role {
    BASIC;

    public static Role from(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}
