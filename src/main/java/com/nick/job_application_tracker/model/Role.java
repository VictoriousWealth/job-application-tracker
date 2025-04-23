package com.nick.job_application_tracker.model;

public enum Role {
    BASIC;

    public String getName() {
        return this.name().toUpperCase();
    }
}
