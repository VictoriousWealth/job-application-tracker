# Architecture Summary

This file is a short, portfolio-style summary of the repository as it exists today.

## Purpose

The project is a Spring Boot application for managing the full job search lifecycle:

- account creation and authentication
- job application tracking
- resume and cover letter management
- attachments and supporting documents
- recruiter communication history
- interview and assessment scheduling
- follow-up reminders
- audit logging and operational traceability
- a bundled React workspace compiled into the same Spring Boot application

## Architecture

The system follows a standard layered Spring architecture:

- `controller`: external API surface
- `service`: business rules and orchestration
- `repository`: persistence and search/query logic
- `model`: JPA entities and enums
- `dto`: stable API contracts
- `config`: security, JWT, auditing, logging, and OpenAPI
- `frontend`: React/Vite source for the browser workspace
- `handler`: centralized exception translation

## Why This Structure

The implemented design is a layered application that is:

- maintainable: responsibilities separated by layer and domain
- auditable: important mutations should leave a trace
- reusable: documents, sources, locations, and skills can be shared across workflows
- extensible: ready for analytics, exports, dashboards, and AI-assisted features
- directly usable: the bundled frontend consumes the secured API as a same-origin compiled artifact without requiring a second production deployment

## Product Outcome

The system gives a user one place to answer the practical questions of a job search:

- Which roles have I applied to?
- Which documents did I use?
- Who have I spoken to?
- What follow-up is due next?
- How is my pipeline progressing over time?

## Important Note

The repository is functionally working and test-green locally. The main remaining gaps are operational hardening and future expansion rather than missing core workflows.
