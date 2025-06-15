# 🎮 Controller Layer — `com.nick.job_application_tracker.controller`

This directory contains the REST controllers that define the **HTTP API endpoints** of the JobTrackr application.

Controllers act as entry points for clients. They handle incoming requests, validate input, interact with service classes, and return responses in the form of DTOs. All business logic is delegated to the service layer.

---

## 🔐 Authentication and User Management

* [`AuthController.java`](./AuthController.java) — Manages user signup, login, JWT generation/refresh, and current user retrieval.
* [`UserController.java`](./UserController.java) — Handles user self-service (`/me`) and admin operations like enabling/disabling accounts.

---

## 💼 Core Application Controllers

* [`JobApplicationController.java`](./JobApplicationController.java) — Create, fetch, and view job applications.
* [`FollowUpReminderController.java`](./FollowUpReminderController.java) — CRUD for reminders to follow up on job applications.
* [`ScheduledCommunicationController.java`](./ScheduledCommunicationController.java) — Handle scheduled events such as interviews and assessments.
* [`SkillTrackerController.java`](./SkillTrackerController.java) — Track skills per job application.
* [`ApplicationTimelineController.java`](./ApplicationTimelineController.java) — Log significant events like submission or cancellation.

---

## 📁 Supporting Data Controllers

* [`JobSourceController.java`](./JobSourceController.java) — Manage job sources like LinkedIn, Glassdoor, Indeed, etc.
* [`LocationController.java`](./LocationController.java) — CRUD for job location metadata (city and country).
* [`ResumeController.java`](./ResumeController.java) — Upload and manage user resumes.
* [`CoverLetterController.java`](./CoverLetterController.java) — Create, list, and delete cover letters.
* [`AttachmentController.java`](./AttachmentController.java) — Manage file uploads such as offer letters or job descriptions.

---

## 📜 Logging and Auditing

* [`AuditLogController.java`](./AuditLogController.java) — Read-only view of audit trail entries for user/system actions.
* [`CommunicationLogController.java`](./CommunicationLogController.java) — Logs communication interactions (email, phone, LinkedIn) with employers.

---

## 🧩 Design Notes

* All controllers:

  * Use `ResponseEntity` for full control of status codes and payloads
  * Leverage `@Valid` for DTO validation
  * Adhere to RESTful path conventions (e.g., `/api/job-applications/{id}`)
* Sensitive actions like signup/login use appropriate HTTP status codes and messages
* Swagger/OpenAPI annotations are encouraged but not required
* Custom errors are standardized via [`GlobalExceptionHandler`](../handler/GlobalExceptionHandler.java)

---

## 🔐 Security

* Most endpoints require JWT-based authentication
* Authenticated user is resolved via Spring Security’s `SecurityContextHolder`
* Admin-specific actions are gated via role logic in the service layer (not enforced directly in controllers)

---

## 🧾 Example Error Response

All errors return the same format defined in `ErrorResponseDTO`:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed: email must not be blank",
  "path": "/api/users/signup",
  "timestamp": "2025-06-15T12:34:56.123Z"
}
```

---

## 📁 File Structure

```
📦 controller/
 ┣ 📄 AuthController.java
 ┣ 📄 UserController.java
 ┣ 📄 JobApplicationController.java
 ┣ 📄 FollowUpReminderController.java
 ┣ 📄 ScheduledCommunicationController.java
 ┣ 📄 SkillTrackerController.java
 ┣ 📄 ApplicationTimelineController.java
 ┣ 📄 JobSourceController.java
 ┣ 📄 LocationController.java
 ┣ 📄 ResumeController.java
 ┣ 📄 CoverLetterController.java
 ┣ 📄 AttachmentController.java
 ┣ 📄 CommunicationLogController.java
 ┗ 📄 AuditLogController.java
```

---

## 📚 See Also

* [`GlobalExceptionHandler`](../handler/GlobalExceptionHandler.java)
* [`ErrorResponseDTO`](../dto/ErrorResponseDTO.java)
* [`service/`](../service/) for the underlying business logic
* [`dto/`](../dto/) for the input/output structures used in all endpoints

---
