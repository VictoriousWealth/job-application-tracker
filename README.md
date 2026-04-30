# JobTrackr Backend

This repository is the Spring Boot backend for a job application tracking system. The finished product is meant to let a user manage the full lifecycle of a job search in one place: applications, resumes, cover letters, communications, reminders, interviews, attachments, skills, audit history, and reporting-ready metadata.

## Status

The documentation in this repository describes the intended completed system.

The implementation itself is still in transition and does not yet fully match the target design described here. Treat the docs as the product and architecture reference, not as a guarantee that every code path is already complete.

## Target Capabilities

- User signup, login, session/token lifecycle, and profile management
- End-to-end job application tracking from draft to offer or rejection
- Reusable document library for resumes, cover letters, and attachments
- Communication history across email, phone, LinkedIn, and in-person touchpoints
- Scheduling and reminder workflows for interviews, assessments, and follow-ups
- Searchable metadata for sources, locations, and skills
- Auditable CRUD activity and administrator visibility into user/account state
- API-first design suitable for a future web or mobile client

## Documentation Map

- [docs/README.md](./docs/README.md): documentation index
- [docs/SYSTEM_FEATURES.md](./docs/SYSTEM_FEATURES.md): complete target feature set, expected results, and expected effects
- [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md): target architecture and package responsibilities
- [HELP.md](./HELP.md): developer quickstart and local setup
- [CVREADME.md](./CVREADME.md): concise architecture summary for portfolio or interview use
- [scripts/README.md](./scripts/README.md): developer utility scripts

Package-level READMEs are also present under `src/main/java/com/nick/job_application_tracker/...` for controllers, DTOs, models, repositories, services, configuration, exceptions, handlers, and utilities.

## Tech Stack

- Java 17
- Spring Boot 3
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- Gradle Kotlin DSL
- OpenAPI / Swagger
- Logback with JSON logging

## Repository Shape

The codebase is organized around standard Spring backend layers:

- `controller`: HTTP endpoints
- `service`: business logic and orchestration
- `repository`: persistence and custom queries
- `model`: JPA entities and enums
- `dto`: request and response contracts
- `config`: security, JWT, OpenAPI, auditing, and filters
- `handler`: centralized exception handling
- `scripts`: utility scripts for testing coverage and scaffolding
- `docs`: product and architecture documentation

## Local Development

See [HELP.md](./HELP.md) for the full setup guide. At a minimum, local development expects:

- Java 17
- PostgreSQL
- environment variables or a `.env` file for:
  - `SPRING_PROFILES_ACTIVE`
  - `POSTGRES_HOST`
  - `POSTGRES_PORT`
  - `POSTGRES_DB`
  - `POSTGRES_USER`
  - `POSTGRES_PASSWORD`

Typical commands:

```bash
./gradlew test
./gradlew bootRun
```

## Product Direction

The intended finished system is more than a CRUD demo. It is meant to become an operational workspace for job searching:

- a single source of truth for every application
- fewer missed follow-ups and deadlines
- consistent document reuse and version tracking
- cleaner historical records for reflection, analytics, and reporting
- a backend foundation for future dashboards, exports, and AI-assisted features
