# Mapper Layer

This package contains converters between JPA entities and DTO contracts.

## Responsibility

Mappers should:

- turn inbound DTOs into entities or entity updates
- turn entities into response or detail DTOs
- keep transport concerns out of entities
- keep persistence concerns out of DTOs

## Resource Coverage

The mapper layer is expected to support the same main domain resources as the DTO and model layers:

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
- users

## Design Rules

- mapping logic should stay deterministic and side-effect free
- mappers should not perform repository lookups unless there is a very strong reason
- business rules belong in services, not in mappers
- security-sensitive fields must never be leaked by entity-to-DTO conversion
- partial update support should be explicit and limited to allowed fields

## Current State Note

This package is now intentionally resource-specific. Some mappers are static utility-style helpers and others are Spring components, but the unused generic base abstraction has been removed because mapper signatures vary by resource and related entities.
