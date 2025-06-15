# Services

This directory contains the business logic layer of the JobTrackr application.  
Services orchestrate operations between controllers, repositories, and mappers, handling data processing, validation, and audit logging.

---

## Core Application Services:

- [JobApplicationService.java](./JobApplicationService.java) — Manages job application creation, retrieval, and deletion.
- [FollowUpReminderService.java](./FollowUpReminderService.java) — Handles follow-up reminders linked to job applications.
- [ScheduledCommunicationService.java](./ScheduledCommunicationService.java) — Manages scheduled events like interviews and assessments.
- [SkillTrackerService.java](./SkillTrackerService.java) — Tracks skills associated with job applications.

---

## User and Authentication Services:

- [UserService.java](./UserService.java) — Manages user account operations (view, update, deactivate, enable/disable).
- [AuditLogService.java](./AuditLogService.java) — Logs create, update, and delete operations throughout the system for traceability.

---

## Supporting Services:

- [AttachmentService.java](./AttachmentService.java) — Manages file attachments related to job applications.
- [CommunicationLogService.java](./CommunicationLogService.java) — Manages communication events (emails, calls, meetings).
- [CoverLetterService.java](./CoverLetterService.java) — Manages cover letter creation and retrieval.
- [JobSourceService.java](./JobSourceService.java) — Manages job sources (e.g., LinkedIn, Indeed).
- [LocationService.java](./LocationService.java) — Manages city and country locations for job applications.
- [ResumeService.java](./ResumeService.java) — Handles resume storage and retrieval.
- [ApplicationTimelineService.java](./ApplicationTimelineService.java) — Manages timeline events (submitted, updated, cancelled) for applications.

---

## Notes:
- Services interact with repositories to persist and retrieve entities.
- Many services integrate `AuditLogService` to automatically log critical user/system actions.
- Mapping between entities and DTOs is handled before passing data to or from the controller layer.

---

## 📚 See Also

- [`controller/`](../controller/README.md) — Defines API endpoints that delegate to these services.
- [`repository/`](../repository/README.md) — Interfaces for database access used within services.
- [`mapper/`](../mapper/README.md) — Translates between DTOs and entities for service input/output.
- [`dto/`](../dto/README.md) — Data Transfer Objects passed into and returned from services.
- [`model/`](../model/README.md) — JPA entities manipulated and persisted by services.
- [`handler/`](../handler/README.md) — Centralized error handling used across services and controllers.

---