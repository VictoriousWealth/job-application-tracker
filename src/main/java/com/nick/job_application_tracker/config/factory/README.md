# API Response Factory

This package contains small helpers used to build repeatable OpenAPI response objects.

## Contents

- `ApiResponseFactory`: creates success and error `ApiResponse` instances with shared schema references and example payloads

## Responsibility

This package keeps documentation-only response construction out of the main OpenAPI configuration class so response examples and schema wiring stay consistent.

## Current State Note

The code here supports generated API docs, not request handling at runtime.
