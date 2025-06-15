# 🧩 Services – `com.nick.job_application_tracker.service`

This package contains the **business logic layer** of the **JobTrackr** application.
Services coordinate data flow between controllers, repositories, and mappers, while applying validation, ownership checks, and audit logging.

---

## 🛠 What Services Do

* Receive DTOs from controllers and convert them to entities
* Perform validation and handle relational consistency (e.g. ensure referenced `JobApplication` exists)
* Persist data via repositories
* Log operations through the `AuditLogService`
* Return DTOs back to controllers for safe and structured API responses

---

## 🗂 Service Classes Overview

### 📌 Core Domain Services

* [`JobApplicationService`](./JobApplicationService.java)
  Creates and manages job applications, resolving relationships like location, resume, and source.

* [`ApplicationTimelineService`](./ApplicationTimelineService.java)
  Records key lifecycle events for job applications (e.g., submitted, updated, cancelled).

* [`FollowUpReminderService`](./FollowUpReminderService.java)
  Manages reminder entries tied to job applications, used for follow-ups.

* [`ScheduledCommunicationService`](./ScheduledCommunicationService.java)
  Manages scheduled events like interviews, phone calls, or assessments.

* [`SkillTrackerService`](./SkillTrackerService.java)
  Links specific skills to applications for tracking requirements or improvements.

* [`CommunicationLogService`](./CommunicationLogService.java)
  Logs communication events such as emails, LinkedIn messages, and calls.

* [`AttachmentService`](./AttachmentService.java)
  Manages file-based attachments such as job descriptions or offer letters.

### 🧑‍💼 User-Facing Services

* [`UserService`](./UserService.java)
  Provides user-level operations like self-update, enable/disable, and listing users.

* [`AuditLogService`](./AuditLogService.java)
  Automatically logs `CREATE`, `UPDATE`, and `DELETE` actions system-wide, tied to the authenticated user.

### 🗺️ Supporting Metadata Services

* [`JobSourceService`](./JobSourceService.java)
  Allows creation and management of job sources (e.g., LinkedIn, Indeed).

* [`LocationService`](./LocationService.java)
  Handles location reuse and ensures cities/countries are not duplicated in the DB.

* [`ResumeService`](./ResumeService.java)
  Manages uploaded resumes and links them to applications.

* [`CoverLetterService`](./CoverLetterService.java)
  Stores cover letters used for job applications.

---

## 📚 See Also

* [`repository/`](../repository/) – Database layer powered by Spring Data JPA
* [`mapper/`](../mapper/) – Maps between DTOs and domain entities
* [`dto/`](../dto/) – Data Transfer Objects used across services and controllers
* [`model/`](../model/) – JPA-annotated domain models and enums
* [`controller/`](../controller/) – Exposes REST APIs using services
* [`handler/`](../handler/) – Global exception handling and validation support

---
