# Resource Files

This directory contains runtime configuration, logging resources, and the bundled static frontend.

## Files

- `application.yaml`: shared Spring configuration
- `application-dev.yaml`: development profile settings
- `application-prod.yaml`: production profile settings
- `logback-spring.xml`: logging configuration
- `static/`: server-served frontend assets (`index.html`, `styles.css`, `app.js`)

## Intended Use

- keep shared defaults in `application.yaml`
- use profile-specific files for environment-dependent settings
- source secrets and database credentials from environment variables or `.env`
- keep logging structured and consistent across environments
- keep the bundled frontend same-origin with the API so JWT-based requests can use the existing backend directly
