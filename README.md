# JobTrackr

This repository is a Spring Boot job application tracking system with both the backend API and a bundled server-served frontend. It lets a user manage the full lifecycle of a job search in one place: applications, resumes, cover letters, communications, reminders, interviews, attachments, skills, audit history, and reporting-ready metadata.

## Status

The repository now implements the documented core workflows, secured API surface, bundled frontend workspace, extended read models, and behavior-focused test suite. The remaining work is mostly operational hardening, browser-level UX polish, or future product expansion, not structural rework.

## Core Capabilities

- User signup, login, session/token lifecycle, and profile management
- End-to-end job application tracking from draft to offer or rejection
- Reusable document library for resumes, cover letters, and attachments
- Communication history across email, phone, LinkedIn, and in-person touchpoints
- Scheduling and reminder workflows for interviews, assessments, and follow-ups
- Searchable metadata for sources, locations, and skills
- Auditable CRUD activity and administrator visibility into user/account state
- API-first design that can support additional web, mobile, or automation clients
- Bundled browser workspace for the current backend without requiring a separate frontend toolchain

## Documentation Map

- [docs/README.md](./docs/README.md): documentation index
- [docs/SYSTEM_FEATURES.md](./docs/SYSTEM_FEATURES.md): implemented feature set, expected results, and expansion areas
- [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md): current architecture and package responsibilities
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

## Frontend Surface

The bundled frontend at `src/main/resources/static` provides:

- authentication flows against the existing JWT API
- dashboard, analytics, recommendations, and calendar views
- application creation, selection, status updates, and related-record management
- document library and account management views
- admin-only user and audit screens when the signed-in user has `ADMIN`

## Repository Shape

The codebase is organized around standard Spring backend layers:

- `controller`: HTTP endpoints
- `service`: business logic and orchestration
- `repository`: persistence access and ownership-aware query methods
- `model`: JPA entities and enums
- `dto`: request and response contracts
- `config`: security, JWT, OpenAPI, auditing, and filters
- `src/main/resources/static`: bundled frontend shell, styles, and client-side API integration
- `handler`: centralized exception handling
- `scripts`: utility scripts for development and coverage
- `docs`: product and architecture documentation

## Local Development

See [HELP.md](./HELP.md) for the full setup guide. At a minimum, local development expects:

- Java 17
- PostgreSQL
- environment variables or a `.env` file for:
  - `SPRING_PROFILES_ACTIVE`
  - `JWT_SECRET`
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

Once the app is running, open `http://localhost:8080/` to use the bundled frontend.

For local non-test runs, set `JWT_SECRET` explicitly so tokens remain valid across restarts.

## How To Run

1. Create a `.env` file in the repository root.

```env
SPRING_PROFILES_ACTIVE=dev
JWT_SECRET=replace-with-at-least-32-bytes-or-base64
DDL_AUTO=create
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=jobtrackr
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
```

2. Make sure PostgreSQL is running and the `POSTGRES_DB` database already exists.

If you are starting with a fresh database, `DDL_AUTO=create` is the simplest local option for the first run.
If you are pointing at an older pre-UUID database, do not use `update`: Hibernate cannot safely migrate the old integer/identity schema to UUID columns.

3. Start the application.

```bash
./gradlew bootRun
```

4. Open the app in your browser.

```text
http://localhost:8080/
```

5. Optional: run the automated tests in a separate shell.

```bash
./gradlew test
```

Useful local URLs:

- `http://localhost:8080/`: bundled frontend
- `http://localhost:8080/swagger-ui/index.html`: Swagger UI

Legacy database note:

- If your local PostgreSQL database was created before the UUID refactor, either drop and recreate that database for local use or perform an explicit SQL migration.
- The app now defaults `DDL_AUTO` to `none` when the variable is not set so it does not keep attempting invalid integer-to-UUID schema mutations on startup.

## Product Direction

The system is more than a CRUD demo. It already operates as an end-to-end job-search workspace and is positioned for incremental expansion:

- a single source of truth for every application
- fewer missed follow-ups and deadlines
- consistent document reuse and version tracking
- cleaner historical records for reflection, analytics, and reporting
- a foundation for deeper dashboards, exports, and AI-assisted features
