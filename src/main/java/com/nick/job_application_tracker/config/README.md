# Configuration Layer

This package contains cross-cutting runtime configuration for the backend.

## Main Concerns

- `SecurityConfig`: security filter chain and authorization setup
- `JwtService`: token generation and validation support
- `CustomJwtAuthenticationToken`: Spring Security authentication token type
- `CustomJwtAuthenticationProvider`: authentication provider for JWT-backed auth
- `CustomJwtAuthFilter`: request filter for auth processing
- `LoggingFilter`: request logging with request IDs
- `JpaAuditingConfig` and `AuditorAwareImpl`: entity auditing support
- `OpenApiConfig`: generated API documentation configuration
- `ApiResponseFactory`: shared API response construction support

## Target Outcome

The finished configuration layer should provide:

- secure authentication and authorization boundaries
- request-scoped user identity
- structured request logging
- predictable OpenAPI output
- automatic auditing metadata on persisted entities

## Current State Note

Security and documentation are among the most sensitive parts of the repository. Treat this package as foundational infrastructure and keep its behavior aligned with the product-level requirements in `docs/SYSTEM_FEATURES.md`.
