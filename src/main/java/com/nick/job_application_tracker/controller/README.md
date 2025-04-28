# Controllers

This directory defines the REST API endpoints of the JobTrackr application.  
Controllers handle incoming HTTP requests, interact with services, and return responses using DTOs.

---

## Authentication and User Management:

- [AuthController.java](./AuthController.java) — Manages user signup, login, token refresh, and authenticated user info.
- [UserController.java](./UserController.java) — Manages user profile, updates, soft-deletion, and admin user management.

---

## Core Application Controllers:

- [JobApplicationController.java](./JobApplicationController.java) — Manages creation and retrieval of job applications.
- [FollowUpReminderController.java](./FollowUpReminderController.java) — Manages follow-up reminders for applications.
- [ScheduledCommunicationController.java](./ScheduledCommunicationController.java) — Manages scheduled communications (e.g., interviews).
- [SkillTrackerController.java](./SkillTrackerController.java) — Manages skills associated with job applications.
- [ApplicationTimelineController.java](./ApplicationTimelineController.java) — Tracks timeline events for job applications.

---

## Supporting Data Controllers:

- [JobSourceController.java](./JobSourceController.java) — Manages job sources like LinkedIn, Indeed, Glassdoor.
- [LocationController.java](./LocationController.java) — Manages locations (city, country) linked to applications.
- [ResumeController.java](./ResumeController.java) — Manages resumes.
- [CoverLetterController.java](./CoverLetterController.java) — Manages cover letters.
- [AttachmentController.java](./AttachmentController.java) — Manages file attachments.

---

## Logging and Audit:

- [AuditLogController.java](./AuditLogController.java) — Provides access to system audit logs (create, update, delete events).
- [CommunicationLogController.java](./CommunicationLogController.java) — Tracks communications between applicants and employers.

---

## Notes:
- Controllers follow **RESTful conventions**.
- All API endpoints are grouped under `/api/...` routes.
- DTOs are used for request and response bodies to ensure a clean separation between internal models and exposed data.
- Authentication is handled via **JWT tokens**.
