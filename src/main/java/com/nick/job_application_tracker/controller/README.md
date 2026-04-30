# Controller Layer

This package contains the REST API entry points for the backend.

## Responsibility

Controllers should:

- define endpoint paths and HTTP methods
- accept validated DTOs
- delegate business rules to services
- return stable response DTOs
- avoid embedding persistence or domain logic directly

## Target Endpoint Groups

- `AuthController`: signup, login, token refresh, and current-user auth state
- `UserController`: profile management and admin-visible account operations
- `JobApplicationController`: core application CRUD, paging, and lifecycle updates
- `ResumeController`: reusable resume management
- `CoverLetterController`: reusable cover letter management
- `AttachmentController`: extra file records tied to applications
- `CommunicationLogController`: recruiter and employer communication history
- `ScheduledCommunicationController`: interviews, calls, and assessments
- `FollowUpReminderController`: next-action reminders
- `ApplicationTimelineController`: application milestone history
- `SkillTrackerController`: skills tied to an application
- `JobSourceController`: normalized origin/source records
- `LocationController`: normalized location records
- `AuditLogController`: audit visibility and operational history
- `InsightsController`: dashboard summaries, conversion analytics, and next-step recommendations
- `CalendarController`: upcoming event views and ICS calendar export
- `ExportController`: workspace export in JSON, CSV, and PDF formats
- `MatchingController`: document and skill fit analysis for a job application

## Design Expectations

In the finished system, controllers should expose:

- consistent `/api/...` routing
- pageable list responses where collections can grow
- authenticated user scoping for user-owned resources
- explicit admin-only routes for administrative actions
- structured error responses via the global exception handler

## Notes On Current State

The package already expresses the intended API surface, but some controllers still reflect in-progress refactoring. Use this README as the target responsibility map for the controller layer.
