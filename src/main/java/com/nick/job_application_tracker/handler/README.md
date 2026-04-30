# Exception Handling

This package centralizes translation from backend exceptions into HTTP responses.

## Main Component

- `GlobalExceptionHandler`: the repository-wide `@RestControllerAdvice`

## Target Behavior

The handler layer should ensure that:

- clients receive predictable JSON errors
- validation failures return field-level detail
- authorization and authentication failures are distinct
- not-found, conflict, rate-limit, and service-unavailable scenarios map cleanly to HTTP status codes
- every error can be correlated with a request identifier in logs

## Error Response Shape

The standard outward error contract is `ErrorResponseDTO`, which includes:

- `status`
- `error`
- `message`
- `path`
- `timestamp`
- `requestId`
- `fieldErrors` when validation details exist

## Design Goal

Controllers and services should throw meaningful exceptions and rely on this package to convert them into stable API responses instead of constructing error payloads manually in each endpoint.
