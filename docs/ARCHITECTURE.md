# Architecture
This document describes the current architecture of the application and the main design constraints it follows.

## Architectural Style

The repository is organized as a layered Spring Boot application with a bundled same-origin frontend:

- API layer: controllers and DTO contracts
- business layer: services and domain orchestration
- persistence layer: repositories and ownership-aware query logic
- domain layer: JPA entities and enums
- platform layer: security, JWT, auditing, logging, OpenAPI, and exception handling
- presentation layer: static frontend assets under `src/main/resources/static`

The application is API-first and can serve its bundled frontend, additional clients, automation scripts, or later integrations without exposing persistence concerns directly.

## Request Flow

The primary request path is:

1. A client calls a REST endpoint.
2. Spring Security authenticates the request.
3. The controller validates and accepts a DTO.
4. The service applies business rules and ownership checks.
5. Repositories load and persist entities.
6. Mappers convert entities into response DTOs.
7. The handler layer turns exceptions into stable API error responses.
8. The bundled frontend renders or updates workspace state from the returned JSON.

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
- filtering, recency, and reporting-oriented lookup methods where needed

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
- public asset allowlisting for the bundled frontend shell

### `src/main/resources/static`

Contains the zero-build browser client:

- `index.html`: app shell
- `styles.css`: visual system and layout
- `app.js`: client-side state, fetch logic, and rendering

### `handler`

Standardizes error responses and maps exceptions to HTTP status codes.

## Data Ownership Model

The ownership boundary is simple:

- a normal user owns their job search workspace
- resources tied to an application should normally resolve back to that same user
- admin-only operations should be explicitly separate from normal user actions
- audit logs should identify who performed the action

## Persistence Conventions

The persistence model uses:

- UUID primary keys
- soft deletion for history-preserving resources
- timestamps for creation and updates
- explicit foreign-key relationships between applications and related records
- normalized reference entities for source and location

## Operational Expectations

The application should support:

- request-scoped authentication context
- structured logs with request IDs
- consistent JSON error payloads
- profile-based environment configuration
- pageable collection endpoints
- testable service and repository boundaries
- a same-origin frontend that can use the secured API without a second deployment artifact

## Current State Note

The codebase is now largely aligned with this design. The remaining gaps are more about deployment, browser-level UX refinement, future integrations, and product expansion than unresolved package structure.
