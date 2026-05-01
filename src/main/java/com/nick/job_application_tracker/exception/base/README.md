# Base API Exceptions

This package contains the root abstraction for application exceptions.

## Contents

- `ApiException`: shared base type for HTTP-oriented exceptions raised by the application

## Responsibility

The base exception contract provides a consistent foundation for client and server exception hierarchies so handlers can produce uniform error responses.

## Current State Note

New domain failures should usually extend the client or server exception packages rather than introducing unrelated runtime exceptions.
