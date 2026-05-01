# Repository Interfaces

This package contains the active Spring Data repository interfaces used by the application at runtime.

## Contents

- `ApplicationTimelineRepository`
- `AttachmentRepository`
- `AuditLogRepository`
- `CommunicationLogRepository`
- `CoverLetterRepository`
- `FollowUpReminderRepository`
- `JobApplicationRepository`
- `JobSourceRepository`
- `LocationRepository`
- `ResumeRepository`
- `ScheduledCommunicationRepository`
- `SkillTrackerRepository`
- `UserRepository`

## Responsibility

These interfaces provide the canonical persistence entry points for services, including CRUD behavior, ownership-aware lookups, and UUID-based entity retrieval.

## Current State Note

This package is the live repository surface for the application and should remain consistent with the UUID-only ownership-scoped model.
