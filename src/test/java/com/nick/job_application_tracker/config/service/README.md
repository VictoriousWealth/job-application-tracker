# Configuration Service Tests

This package contains tests for services that live under the configuration layer rather than the main business service layer.

## Contents

- `JwtServiceTest`: token generation, parsing, and validation behavior

## Responsibility

Tests here should verify security-critical behavior such as secret handling, expiry, claim extraction, and token validity rules.
