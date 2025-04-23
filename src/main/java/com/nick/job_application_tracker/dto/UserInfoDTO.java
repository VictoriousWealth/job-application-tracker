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
}
