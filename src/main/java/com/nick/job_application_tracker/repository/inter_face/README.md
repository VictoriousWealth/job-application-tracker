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

The `inter_face` package name is legacy. Despite the naming, this is the live repository surface and should remain consistent with the current UUID-only model.
