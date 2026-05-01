package com.nick.job_application_tracker.config.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.CustomJwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomJwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HDR_KEY = "Authorization";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    public static final List<String> PUBLIC_URL_PATTERNS = List.of(
        "/api/auth/signup",
        "/api/auth/login",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/index.html",
        "/app.js",
        "/styles.css",
        "/",
        "/favicon.ico",
        "/error"
    );

    private final AuthenticationManager authenticationManager;

    public CustomJwtAuthFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(
        @SuppressWarnings("null") HttpServletRequest request, 
        @SuppressWarnings("null") HttpServletResponse response, 
        @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {
        // allow all public auth routes through without filtering
        String path = request.getRequestURI();
        if (isPublicUrl(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // return unauthorized to clients who attempt to access protected endpoints
        if (request.getHeader(AUTHORIZATION_HDR_KEY) == null) {
            sendErrorResponse(response, "Authorization header is missing");
            return;
        }

        // return unauthorized to clients that attempt to access protected endpoints
        // using anything other than a bearer token.
        if (!request.getHeader(AUTHORIZATION_HDR_KEY).startsWith("Bearer ")) {
            sendErrorResponse(response, "Unauthorized");
            return;
        }

        try {
            String token = request.getHeader(AUTHORIZATION_HDR_KEY).substring("Bearer ".length());
            CustomJwtAuthenticationToken authentication = new CustomJwtAuthenticationToken(token);
            Authentication authResult = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            sendErrorResponse(response, "Unauthorized");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    private boolean isPublicUrl(String path) {
        return PUBLIC_URL_PATTERNS.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
