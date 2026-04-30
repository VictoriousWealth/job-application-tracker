# Service Implementations In `inter_face`

Despite the package name, this directory currently contains service classes rather than only interfaces.

## Intended Responsibilities

The services in this package are meant to handle workflows for:

- users and authentication-adjacent account operations
- job applications
- resumes and cover letters
- attachments
- communication logs
- scheduled communications
- reminders and timelines
- skills
- sources and locations
- audit logging

## Expectations For These Services

Each service should:

- operate on DTOs rather than exposing entities to controllers
- validate ownership and related-resource existence
- keep query logic inside repositories
- keep mapping logic inside mappers
- emit audit events for meaningful mutations

## Naming Note

The package name is a legacy artifact of the current refactor. The long-term design should make the distinction between service contracts and implementations explicit and consistent.
