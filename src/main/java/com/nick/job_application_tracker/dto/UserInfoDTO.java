package com.nick.job_application_tracker.dto;

import java.util.Set;

public class UserInfoDTO {
    public Long id;
    public String email;
    public boolean enabled;
    public Set<String> roles;

    public UserInfoDTO(Long id, String email, boolean enabled, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.enabled = enabled;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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
