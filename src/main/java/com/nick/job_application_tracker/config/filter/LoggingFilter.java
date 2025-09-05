package com.nick.job_application_tracker.config.filter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nick.job_application_tracker.common.LogKeys;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static net.logstash.logback.argument.StructuredArguments.kv;

@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();
        // 1. Extract or generate request ID
        String requestId = Optional.ofNullable(request.getHeader(REQUEST_ID_HEADER))
                                .orElse(UUID.randomUUID().toString());

        // 2. Include it in response headers
        response.setHeader(REQUEST_ID_HEADER, requestId);
        MDC.put(LogKeys.REQUEST_ID, requestId);


        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - start;

            String method = request.getMethod();
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            int status = response.getStatus();

            // Optional: get authenticated user
            String user = "anonymous";
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                Object principal = auth.getPrincipal();
                if (principal instanceof UserDetails userDetails) {
                    user = userDetails.getUsername();
                } else if (principal instanceof String s) {
                    user = s;
                }
            }

            // 3. Log structured request log with requestId
            log.info("Handled request",
                kv(LogKeys.REQUEST_ID, requestId),
                kv("method", method),
                kv("uri", uri),
                kv("status", status),
                kv("ip", ip),
                kv("userAgent", userAgent),
                kv("durationMs", durationMs),
                kv("user", user)
            );
            MDC.clear();
        }
    }
}
