package com.nick.job_application_tracker.config;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class CustomJwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;
    private final String username;

    public CustomJwtAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.token = token;
        this.username = null;
        setAuthenticated(false);
    }

    public CustomJwtAuthenticationToken(String token, String username, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.token = token;
        this.username = username;
        setAuthenticated(true); // Now authenticated
    }

    @Override
    public Object getCredentials() {
        return token; // The JWT token itself
    }

    @Override
    public Object getPrincipal() {
        return username; // Extracted username from the token
    }
}
