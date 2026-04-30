package com.nick.job_application_tracker.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.nick.job_application_tracker.config.filter.CustomJwtAuthFilter;
import com.nick.job_application_tracker.config.filter.LoggingFilter;
import com.nick.job_application_tracker.config.provider.CustomJwtAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomJwtAuthenticationProvider customJwtAuthenticationProvider;

    public SecurityConfig(CustomJwtAuthenticationProvider customJwtAuthenticationProvider) {
        this.customJwtAuthenticationProvider = customJwtAuthenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000")); // ✅ Set your frontend URL here
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // If using cookies or Authorization header

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 🔹 Disable CSRF (only for APIs; keep it enabled for traditional web apps)
        http.csrf(AbstractHttpConfigurer::disable);

        // 🔹 CORS configuration (enable if frontend is on a different origin)
        http.cors(Customizer.withDefaults());

        // 🔹 Custom Logging Filter (Before AsyncManagerFilter)
        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);
        // 🔹 Authentication Filters
        http.addFilterBefore(new CustomJwtAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);

        // 🔹 Logout Filter (Enables /logout)
        http.logout(logout -> logout.logoutUrl("/logout").permitAll());

        // 🔹 Session Management (Stateless for JWT, Session-Based for OAuth2)
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 🔹 Role-Based Authorization
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers(HttpMethod.GET, "/", "/favicon.ico", "/error").permitAll();
            auth.requestMatchers("/api/auth/login", "/api/auth/signup").permitAll();
            auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll(); // OpenAPI docs
            auth.requestMatchers("/api/users/me").authenticated();
            auth.requestMatchers("/api/users/me/**").authenticated();
            auth.requestMatchers("/api/users/**").hasRole("ADMIN");
            auth.requestMatchers("/api/audit-log/**").hasRole("ADMIN");
            auth.anyRequest().authenticated(); // Protect all other routes
        });

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customJwtAuthenticationProvider));
    }

    @Bean
    public RequestMatcher requestMatcher() {
        return new AntPathRequestMatcher("**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
