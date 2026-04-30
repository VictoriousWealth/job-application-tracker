# DTO Layer

This package contains the API contracts used between clients and the backend.

## DTO Families

The repository is organized around five DTO groups:

- `create`: input payloads used when creating a resource
- `update`: payloads used for full or explicit updates
- `detail`: rich single-resource views
- `response`: lighter list-friendly or summary views
- `special`: auth, user, and error contracts

## Covered Resource Types

The DTO structure is intended to cover the full domain:

- application timeline
- attachments
- audit logs
- communication logs
- cover letters
- follow-up reminders
- job applications
- job sources
- locations
- resumes
- scheduled communications
- skill tracking
- authentication and user profile flows

## Why The Split Exists

The separation is deliberate:

- create DTOs can validate required input for new records
- update DTOs can express mutable fields explicitly
- detail DTOs can expand relationships for single-record views
- response DTOs can stay compact for paged collections
- special DTOs keep auth and error contracts isolated from domain entities

## Rules For This Package

- entities should not be returned directly from controllers
- DTOs should not contain persistence annotations
- validation belongs on inbound DTOs, not on transport-neutral response DTOs
- sensitive fields such as password hashes must never appear in outward-facing DTOs
- error responses should remain consistent with `ErrorResponseDTO`

## Current State Note

The intended structure is clear, but some classes elsewhere in the repository still reference older DTO names. This README describes the DTO model the project should standardize on.
