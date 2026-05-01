package com.nick.job_application_tracker.config.provider;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.nick.job_application_tracker.config.CustomJwtAuthenticationToken;
import com.nick.job_application_tracker.config.service.JwtService;
import com.nick.job_application_tracker.model.User;
import com.nick.job_application_tracker.repository.interfaces.UserRepository;

@Component
public class CustomJwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public CustomJwtAuthenticationProvider(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();
        String username;
        try {
            String[] o = jwtService.validateTokenAndGetUsernameAndRole(token);
            username = o[0];
        } catch (Exception e) {
            throw new AuthenticationException("No such algorithm exist or an invalid key is being used"){};
        }

        if (username == null) throw new AuthenticationException("Invalid JWT Token"){};

        User user = userRepository.findByEmailAndDeletedFalse(username)
            .orElseThrow(() -> new AuthenticationException("Invalid JWT Token") {});
        if (!user.isEnabled()) {
            throw new AuthenticationException("User account is disabled") {};
        }

        String role = user.getRole().name();
        Collection<? extends GrantedAuthority> authorities =
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
        CustomJwtAuthenticationToken customJwtAuthenticationToken =  new CustomJwtAuthenticationToken(token, username, authorities);
        customJwtAuthenticationToken.setDetails(authentication.getDetails());
        return customJwtAuthenticationToken; // Successfully authenticated
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CustomJwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
