package com.nick.job_application_tracker.dto;

public class UserUpdateDTO {
    public String email;
    public String password;
    public boolean enabled;
    
    public UserUpdateDTO(String email, String password) {
        this.email = email;
        this.password = password;
        this.enabled = true;
    }
    
    public UserUpdateDTO(String email, String password, boolean enabled) {
        this(email, password);
        this.enabled = enabled;
    }

    // getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    
}
