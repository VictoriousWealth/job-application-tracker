# Bundled Frontend

This directory contains the server-served frontend for JobTrackr.

## Files

- `index.html`: application shell and view containers
- `styles.css`: visual system, layout, responsive behavior, and motion
- `app.js`: client-side state management, API integration, and UI rendering

## Responsibility

The bundled frontend is intentionally zero-build:

- it is served directly by Spring Boot
- it uses the same origin as the backend API
- it stores the JWT locally and sends it through the existing bearer-auth flow
- it avoids introducing a separate frontend toolchain into the repository
