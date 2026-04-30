# Security Filters

This package contains servlet filters applied around request processing.

## Contents

- `CustomJwtAuthFilter`: validates bearer tokens on protected routes and populates the security context
- `LoggingFilter`: assigns request IDs and writes structured request logs

## Responsibility

These filters enforce authentication and request traceability before controllers execute.

## Current State Note

Filter order matters here because authentication and logging both run as cross-cutting infrastructure.
