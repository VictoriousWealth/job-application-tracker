# Target Architecture

This document describes the intended architecture of the finished backend.

## Architectural Style

The repository is organized as a layered Spring Boot application:

- API layer: controllers and DTO contracts
- business layer: services and domain orchestration
- persistence layer: repositories and custom query logic
- domain layer: JPA entities and enums
- platform layer: security, JWT, auditing, logging, OpenAPI, and exception handling

The goal is an API-first backend that can serve a dedicated frontend, automation scripts, or future integrations without exposing persistence concerns directly.

## Request Flow

The intended request path is:

1. A client calls a REST endpoint.
2. Spring Security authenticates the request.
3. The controller validates and accepts a DTO.
4. The service applies business rules and ownership checks.
5. Repositories load and persist entities.
6. Mappers convert entities into response DTOs.
7. The handler layer turns exceptions into stable API error responses.

## Package Responsibilities

### `controller`

Defines endpoint groups such as:

- authentication and user management
- job applications
- document resources
- communications, schedules, and reminders
- metadata resources such as source and location
- audit visibility

### `dto`

Separates request and response shapes by use case:

- `create`: payloads for new resources
- `update`: full or explicit update payloads
- `detail`: richer single-resource views
- `response`: list-friendly or lightweight views
- `special`: auth, user, and error contracts

### `service`

Owns application rules such as:

- record ownership and user scoping
- relational validation between entities
- audit log generation
- state transitions
- document linkage and reusable resource handling

### `repository`

Provides:

- basic CRUD access through Spring Data JPA
- scoped query methods such as "by user and not deleted"
- custom query implementations for search, recency, and filtering

### `model`

Contains the persistent domain:

- `User`
- `JobApplication`
- `Resume`
- `CoverLetter`
- `Attachment`
- `CommunicationLog`
- `ScheduledCommunication`
- `FollowUpReminder`
- `ApplicationTimeline`
- `SkillTracker`
- `JobSource`
- `Location`
- `AuditLog`

Most domain entities inherit shared auditing and soft-delete behavior from `BaseEntity`.

### `config`

Contains cross-cutting platform concerns:

- security filter chain
- JWT support
- authentication provider
- logging filter
- JPA auditing
- OpenAPI generation

### `handler`

Standardizes error responses and maps exceptions to HTTP status codes.

## Data Ownership Model

The intended ownership boundary is simple:

- a normal user owns their job search workspace
- resources tied to an application should normally resolve back to that same user
- admin-only operations should be explicitly separate from normal user actions
- audit logs should identify who performed the action

## Persistence Conventions

The target persistence model uses:

- UUID primary keys
- soft deletion for history-preserving resources
- timestamps for creation and updates
- explicit foreign-key relationships between applications and related records
- normalized reference entities for source and location

## Operational Expectations

The finished backend should support:

- request-scoped authentication context
- structured logs with request IDs
- consistent JSON error payloads
- profile-based environment configuration
- pageable collection endpoints
- testable service and repository boundaries

## Current State Note

The codebase is still converging on this design. Some packages reflect older naming or partially migrated abstractions. This document captures the architecture the repository should align with, not a claim that every current implementation detail already does.
