# 🎮 Controller Layer — `com.nick.job_application_tracker.controller`

This directory contains the **REST controllers** that define the **HTTP API endpoints** of the JobTrackr application.

Controllers serve as the main **entry points for client requests**. They handle HTTP input, apply validation, call the appropriate services, and return results using DTOs. All core business logic resides in the service layer.

---

## 🔐 Authentication and User Management

* [`AuthController.java`](./AuthController.java)
  Handles **user signup, login, token refresh**, and `/me` endpoint for retrieving authenticated user info.
* [`UserController.java`](./UserController.java)
  Supports **self-service actions** (update, deactivate via `/me`) and **admin user management** (list, enable/disable users).

---

## 💼 Core Application Controllers

* [`JobApplicationController.java`](./JobApplicationController.java)
  Create, list, and fetch job applications by ID.
* [`FollowUpReminderController.java`](./FollowUpReminderController.java)
  Manage reminders to follow up on job applications.
* [`ScheduledCommunicationController.java`](./ScheduledCommunicationController.java)
  Schedule events like **interviews** or **coding assessments**.
* [`SkillTrackerController.java`](./SkillTrackerController.java)
  Track **skills** associated with job applications.
* [`ApplicationTimelineController.java`](./ApplicationTimelineController.java)
  Log application **timeline events** like submission, interviews, or cancellation.

---

## 📁 Supporting Data Controllers

* [`JobSourceController.java`](./JobSourceController.java)
  Manage job sources such as **LinkedIn**, **Indeed**, and **referrals**.
* [`LocationController.java`](./LocationController.java)
  Handle **city/country** metadata tied to job postings.
* [`ResumeController.java`](./ResumeController.java)
  Upload, list, and delete **resumes**.
* [`CoverLetterController.java`](./CoverLetterController.java)
  Manage user cover letters.
* [`AttachmentController.java`](./AttachmentController.java)
  Manage uploaded **attachments** like offer letters or descriptions.

---

## 📜 Logging and Auditing

* [`AuditLogController.java`](./AuditLogController.java)
  Read-only access to system-wide audit trail entries.
* [`CommunicationLogController.java`](./CommunicationLogController.java)
  Log email, phone, in-person, or LinkedIn communications with employers.

---

## 🧩 Design Notes

* ✅ All controllers:

  * Use `@RestController` and follow RESTful conventions
  * Return `ResponseEntity<?>` for precise control over HTTP responses
  * Validate inputs using `@Valid`
  * Group paths under `/api/...` consistently
* 🔐 JWT authentication is required for most endpoints and injected via Spring Security's `SecurityContextHolder`
* ⚙️ Authorization logic (e.g. admin checks) is implemented at the service level
* 🎯 Swagger/OpenAPI annotations (e.g. `@Operation`, `@ApiResponse`) are **supported** and **recommended**

---

## ⚠️ Global Error Handling

All exceptions are handled uniformly by [`GlobalExceptionHandler`](../handler/GlobalExceptionHandler.java), returning structured `ErrorResponseDTO` output like:

```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You are not allowed to update this resource",
  "path": "/api/users/7",
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

* [`GlobalExceptionHandler`](../handler/GlobalExceptionHandler.java) — Centralized error handling
* [`ErrorResponseDTO`](../dto/ErrorResponseDTO.java) — Unified error response structure
* [`dto/`](../dto/) — Input/output object definitions
* [`service/`](../service/) — Business logic layer beneath controllers
* [`mapper/`](../mapper/) — Conversion utilities between DTOs and entities

---
