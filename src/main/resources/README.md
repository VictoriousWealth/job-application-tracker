# Resource Files

This directory contains runtime configuration, logging resources, and the compiled frontend bundle that Spring Boot serves.

## Files

- `application.yaml`: shared Spring configuration
- `application-dev.yaml`: development profile settings
- `application-prod.yaml`: production profile settings
- `logback-spring.xml`: logging configuration
- `static/`: generated frontend bundle (`index.html`, `assets/*.js`, `assets/*.css`)

## Runtime Expectations

- keep shared defaults in `application.yaml`
- use profile-specific files for environment-dependent settings
- source secrets and database credentials from environment variables or `.env`
- keep logging structured and consistent across environments
- keep the compiled frontend same-origin with the API so JWT-based requests can use the existing backend directly
- keep public frontend assets and protected API routes aligned with the security configuration

Frontend source code does not live here. Edit `frontend/` and rebuild when browser changes are needed.
