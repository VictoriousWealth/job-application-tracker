# Service Implementations

This package contains the concrete runtime business services for the application.

## Contents

- `JobApplicationService`: core application workflow orchestration
- `ResumeService` and `CoverLetterService`: user-owned document management
- `AttachmentService`, `CommunicationLogService`, `FollowUpReminderService`, and `ScheduledCommunicationService`: application-related supporting records
- `ApplicationTimelineService` and `AuditLogServiceImpl`: history and audit tracking
- `JobSourceService`, `LocationService`, `SkillTrackerService`, and `UserService`: supporting domain services

## Responsibility

Concrete services enforce ownership rules, validate references between entities, coordinate repositories and mappers, and trigger side effects such as audit logging or timeline writes.

## Current State Note

This package is the main execution path for business logic after the refactor cleanup. New workflow rules should generally be implemented here.
