# Resource Files

This directory contains runtime configuration and logging resources.

## Files

- `application.yaml`: shared Spring configuration
- `application-dev.yaml`: development profile settings
- `application-prod.yaml`: production profile settings
- `logback-spring.xml`: logging configuration

## Intended Use

- keep shared defaults in `application.yaml`
- use profile-specific files for environment-dependent settings
- source secrets and database credentials from environment variables or `.env`
- keep logging structured and consistent across environments
