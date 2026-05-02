# Repository Layer

This package owns database access for the application.

## Package Split

The repository code is centered on one concern:

- `interfaces`: Spring Data repository interfaces

## Domain Coverage

Repositories are expected for the main user-owned and reference resources:

- applications
- timelines
- reminders
- scheduled communications
- communication logs
- attachments
- resumes
- cover letters
- skills
- sources
- locations
- audit logs
- users

## Design Expectations

Repositories should provide:

- standard CRUD through Spring Data JPA
- scoped queries that respect ownership and soft deletion
- filtering by status, source, location, keywords, and time where useful
- pagination for collection queries

## Rule Of Thumb

Business rules should stay in services. Repositories should focus on loading, storing, and querying data efficiently and predictably.
