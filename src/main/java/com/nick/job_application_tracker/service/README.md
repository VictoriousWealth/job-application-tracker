# Service Layer

This package contains the business logic of the backend.

## Subpackages

- `common`: shared service contracts or abstractions
- `inter_face`: service classes and some legacy naming from earlier iterations
- `implementation`: concrete service implementations introduced during refactoring
- `specialised_common`: domain-specific service contracts

## Responsibility

Services should:

- apply ownership and authorization rules
- validate references between entities
- coordinate repository calls
- invoke mappers for inbound and outbound DTO conversion
- write audit log entries for important changes
- define allowed state transitions and domain workflows

## Long-Term Direction

The package naming currently reflects a repository in transition. The end goal should be a consistent split between service interfaces/contracts and service implementations without leaking older naming into new code.
