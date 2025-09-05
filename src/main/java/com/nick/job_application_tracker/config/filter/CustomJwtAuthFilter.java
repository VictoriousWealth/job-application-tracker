package com.nick.job_application_tracker.config.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nick.job_application_tracker.config.CustomJwtAuthenticationToken;
import com.nick.job_application_tracker.config.service.JwtService;
import com.nick.job_application_tracker.repository.inter_face.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomJwtAuthFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HDR_KEY = "Authorization";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    public static final List<String> PUBLIC_URL_PATTERNS = List.of(
        "/api/auth/signup",
        "/api/auth/login",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html",
        "/",
        "/favicon.ico",
        "/error",
        "/actuator/**",
        "/h2-console/**"
    );

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    public CustomJwtAuthFilter(JwtService jwtService, AuthenticationManager authenticationManager, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
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
            sendErrorResponse(response, "Authroization header is missing");
            return;
        }

        // allow clients with valid jwt token to access protected endpoints
        if (request.getHeader(AUTHORIZATION_HDR_KEY).startsWith("Bearer ")) {
            String token = request.getHeader(AUTHORIZATION_HDR_KEY).substring("Bearer ".length());
            System.out.println("token: " + token);
            CustomJwtAuthenticationToken authentication = new CustomJwtAuthenticationToken(token);
            Authentication authResult = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            filterChain.doFilter(request, response);
            return;
        }


        // return unathorized to clients that attempt to access protected endpoints using anything other than basic or bearer authorization
        if (!request.getHeader(AUTHORIZATION_HDR_KEY).startsWith("Basic ")) {
            sendErrorResponse(response, "Unauthorized");
            return;
        }

        // decoding data to extract credentials from basic auth clients
        String token = new String(Base64.getUrlDecoder().decode(request.getHeader(AUTHORIZATION_HDR_KEY).substring("Basic ".length())));
        System.out.println("username:password " + token);
        String username = token.split(":")[0];

        // return unathorized to clients who attempt to access protected endpoints with invalid username and password combinations
        if(userRepository.findByEmailAndDeletedFalse(username).isEmpty()) {
            sendErrorResponse(response, "Invalid username or password");
            return;
        }

        // attempt to generate jwt token and authenticate it
        try {
            System.out.println("User found");
            String role = userRepository.findByEmailAndDeletedFalse(username).get().getRoles().toArray()[0].toString();
            token = jwtService.generateToken(username, role);
            System.out.println("token generated successfully :" + token);
            CustomJwtAuthenticationToken authentication = new CustomJwtAuthenticationToken(token);
            Authentication authResult = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            response.addHeader(AUTHORIZATION_HDR_KEY, "Bearer " + token);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("NoSuchAlgorithmException or InvalidKeyException");
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
