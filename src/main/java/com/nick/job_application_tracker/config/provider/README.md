# Authentication Provider

This package contains the Spring Security authentication provider used by JWT-based auth.

## Contents

- `CustomJwtAuthenticationProvider`: validates a token through `JwtService`, reloads the user, and derives granted authorities

## Responsibility

The provider translates a raw bearer token into an authenticated Spring Security principal.

## Current State Note

This package is tightly coupled to the token format and current user-loading rules, so changes here should be coordinated with the auth filter and `JwtService`.
