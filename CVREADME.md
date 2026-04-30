# Architecture Summary

This file is a short, portfolio-style summary of the repository.

## Purpose

The project is intended to be a backend API for managing the full job search lifecycle:

- account creation and authentication
- job application tracking
- resume and cover letter management
- attachments and supporting documents
- recruiter communication history
- interview and assessment scheduling
- follow-up reminders
- audit logging and operational traceability

## Target Design

The system follows a standard layered Spring architecture:

- `controller`: external API surface
- `service`: business rules and orchestration
- `repository`: persistence and search/query logic
- `model`: JPA entities and enums
- `dto`: stable API contracts
- `config`: security, JWT, auditing, logging, and OpenAPI
- `handler`: centralized exception translation

## Why This Structure

The intended end state is a backend that is:

- maintainable: responsibilities separated by layer and domain
- auditable: important mutations should leave a trace
- reusable: documents, sources, locations, and skills can be shared across workflows
- extensible: ready for analytics, exports, dashboards, and AI-assisted features

## Product Outcome

Once complete, the system should give a user one place to answer the practical questions of a job search:

- Which roles have I applied to?
- Which documents did I use?
- Who have I spoken to?
- What follow-up is due next?
- How is my pipeline progressing over time?

## Important Note

This summary describes the intended finished system. The current implementation is still being refactored and does not yet fully match that target.
