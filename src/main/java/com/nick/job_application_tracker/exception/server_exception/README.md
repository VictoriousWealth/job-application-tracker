# Server Exceptions

This package contains exceptions that map to 5xx HTTP responses.

## Contents

- `InternalServerErrorException`
- `ServiceUnavailableException`
- `ServerException`

## Responsibility

These exceptions describe unexpected internal failures, unavailable dependencies, or other server-side conditions that are not caused by client input.

## Current State Note

Service code should throw these sparingly and prefer predictable client exceptions when the failure condition is known and actionable.
