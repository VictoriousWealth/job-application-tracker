package com.nick.job_application_tracker.dto.special;

import java.util.Set;
import java.util.UUID;

public class UserDetailDTO {
    public UUID id;
    public String email;
    public boolean enabled;
    public Set<String> roles;

    public UserDetailDTO(UUID id, String email, boolean enabled, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
