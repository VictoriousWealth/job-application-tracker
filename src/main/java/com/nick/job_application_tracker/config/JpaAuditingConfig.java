package com.nick.job_application_tracker.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables JPA auditing for @CreatedDate, @LastModifiedDate, @CreatedBy, and @LastModifiedBy.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaAuditingConfig {
    // No methods needed
}
