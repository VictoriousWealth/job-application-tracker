# Client Exceptions

This package contains exceptions that map to 4xx HTTP responses.

## Contents

- `BadRequestException`
- `UnauthorizedException`
- `ForbiddenException`
- `NotFoundException`
- `ConflictException`
- `ValidationException`
- `RateLimitExceededException`
- `ClientException`

## Responsibility

These exceptions describe failures caused by invalid input, missing permissions, unknown resources, or otherwise incorrect client behavior.

## Current State Note

Resource-specific client exceptions live under the nested `specialised_case` package when a generic 4xx type is not descriptive enough.
