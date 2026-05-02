# Domain Model

This package contains the persistent domain for the application.

## Core Entities

- `User`: account owner for a job-search workspace
- `JobApplication`: the central record for a role or opportunity
- `Resume`: reusable resume metadata and file reference
- `CoverLetter`: reusable cover letter metadata and file reference
- `Attachment`: additional application-specific files
- `CommunicationLog`: inbound and outbound communication history
- `ScheduledCommunication`: future interviews, calls, and assessments
- `FollowUpReminder`: next-action reminders
- `ApplicationTimeline`: milestone and lifecycle events
- `SkillTracker`: skills attached to an application
- `JobSource`: normalized source of an application
- `Location`: normalized geographic or work-location reference
- `AuditLog`: business-level activity trail
- `Role`: authorization role model

## Shared Entity Conventions

Most entities inherit common behavior from `BaseEntity`:

- UUID primary key
- creation and update timestamps
- created-by and updated-by metadata
- optimistic locking support
- soft-delete support for history-preserving resources

## Ownership Model

The domain boundary is:

- a `User` owns their `JobApplication` records
- application-linked entities such as attachments, reminders, schedules, and communications should resolve back to that same user through the application
- normalized resources such as source and location should be reusable where appropriate
- audit logs should point to the user responsible for the action

## Expected Domain Outcomes

When the model is fully supported by the service and repository layers, it should let the system answer questions such as:

- what did the user apply to
- what state is each application in
- what documents were used
- what follow-ups or interviews are coming next
- what communication has already happened
- how did an application progress over time

## Current State Note

The entity set now matches the active service, repository, and integration-test surface. Future domain changes should preserve user ownership, auditability, and UUID-based identity.
