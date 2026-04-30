# Special DTOs

This package contains API payloads that do not fit the standard create, update, response, or detail DTO families.

## Contents

- `LoginRequest` and `SignupRequest`: authentication and account-creation payloads
- `JwtResponse`: bearer token response contract
- `ErrorResponseDTO`: normalized error body returned by exception handling
- `UserDetailDTO` and `UserUpdateDTO`: user-specific account DTOs

## Responsibility

These types support cross-cutting API concerns such as login, signup, token exchange, error responses, and user-account operations.

## Current State Note

Because security and exception handling depend on these DTOs directly, they should stay stable and narrowly scoped.
