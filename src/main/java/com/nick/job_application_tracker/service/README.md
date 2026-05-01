# Service Layer

This package contains the business logic of the backend.

## Subpackages

- `common`: shared service contracts or abstractions
- `interfaces`: shared service contracts such as audit logging
- `implementation`: concrete service implementations introduced during refactoring

## Responsibility

Services should:

- apply ownership and authorization rules
- validate references between entities
- coordinate repository calls
- invoke mappers for inbound and outbound DTO conversion
- write audit log entries for important changes
- define allowed state transitions and domain workflows

## Current Direction

The service layer now uses a straightforward split between reusable contracts in `interfaces` and executable business logic in `implementation`.
