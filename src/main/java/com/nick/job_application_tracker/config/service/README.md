# JWT Support

This package contains token-generation and token-validation logic.

## Contents

- `JwtService`: creates signed JWTs, validates incoming tokens, and extracts claim values such as user email and role

## Responsibility

The package owns the application-specific token contract used by login, refresh, and request authentication.

## Current State Note

Runtime secrets come from configuration, so operational setup is as important as the code here.
