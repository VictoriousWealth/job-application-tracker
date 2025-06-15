# 📦 Data Transfer Objects (DTOs)

This directory contains all **Data Transfer Objects (DTOs)** used in the **JobTrackr** Spring Boot application.
DTOs are used to transfer data between the API and internal layers (e.g., services) while hiding internal entity structures and enforcing validation.

---

## 📄 Application & Timeline DTOs

* [ApplicationTimelineDTO.java](./ApplicationTimelineDTO.java) — Represents timeline events for job applications.
* [AuditLogDTO.java](./AuditLogDTO.java) — Records system-level actions for auditing purposes.

---

## 📎 Attachments, Resumes & Cover Letters

* [AttachmentDTO.java](./AttachmentDTO.java) — Represents a file (currently stored via DB path) linked to a job application.
* [ResumeDTO.java](./ResumeDTO.java) — Contains resume file information.
* [CoverLetterDTO.java](./CoverLetterDTO.java) — Represents a stored cover letter with optional file path and content.

---

## 📋 Job Application DTOs

* [JobApplicationCreateDTO.java](./JobApplicationCreateDTO.java) — Used when creating new job applications.
* [JobApplicationDetailDTO.java](./JobApplicationDetailDTO.java) — Full detail view, including IDs for related entities.
* [JobApplicationResponseDTO.java](./JobApplicationResponseDTO.java) — Client-facing DTO showing resolved names (e.g., resume name).
* [FollowUpReminderCreateDTO.java](./FollowUpReminderCreateDTO.java) — DTO for creating reminders.
* [FollowUpReminderDTO.java](./FollowUpReminderDTO.java) — Represents a follow-up reminder entry.

---

## 📞 Communication DTOs

* [CommunicationLogDTO.java](./CommunicationLogDTO.java) — Logs sent/received messages related to a job application.
* [ScheduledCommunicationCreateDTO.java](./ScheduledCommunicationCreateDTO.java) — For scheduling future communication events (e.g., interviews).
* [ScheduledCommunicationDTO.java](./ScheduledCommunicationDTO.java) — Full representation of scheduled communication.

---

## 🧠 Skills & Job Sources

* [SkillTrackerCreateDTO.java](./SkillTrackerCreateDTO.java) — Used to track skills for a job application.
* [SkillTrackerDTO.java](./SkillTrackerDTO.java) — Represents an existing skill entry.
* [JobSourceCreateDTO.java](./JobSourceCreateDTO.java) — For creating job sources (e.g., LinkedIn).
* [JobSourceDTO.java](./JobSourceDTO.java) — Represents a job source record.

---

## 🔐 Authentication & User DTOs

* [LoginRequest.java](./LoginRequest.java) — Payload for login requests.
* [SignupRequest.java](./SignupRequest.java) — Payload for account registration.
* [JwtResponse.java](./JwtResponse.java) — Contains JWT token after successful authentication.
* [UserInfoDTO.java](./UserInfoDTO.java) — Contains user profile details and roles.
* [UserUpdateDTO.java](./UserUpdateDTO.java) — Used for updating account credentials and status.

---

## 🌍 Miscellaneous

* [LocationDTO.java](./LocationDTO.java) — Represents city and country location details.
* [ErrorResponseDTO.java](./ErrorResponseDTO.java) — Standard structure for API error messages.

---

## ✅ Validation

DTOs use Jakarta Bean Validation for input validation:

| Annotation  | Purpose                                   |
| ----------- | ----------------------------------------- |
| `@NotNull`  | Field must not be `null`                  |
| `@NotBlank` | String must be non-empty and non-null     |
| `@Null`     | Field must be `null` (e.g., auto-gen IDs) |

Validation is applied in controller endpoints using `@Valid`.

---

## 🌐 Timezone Handling

All date-time fields (`LocalDateTime`, `OffsetDateTime`) are assumed to be in **UTC**.

Configured in:

```properties
spring.jackson.time-zone=UTC
```

Clients must send and interpret all timestamps in UTC (e.g., `2025-06-15T14:00:00Z`), ensuring consistent behavior across regions.

---

## ⚙️ Assumptions & Design Notes

| Topic            | Assumption / Practice                                            |
| ---------------- | ---------------------------------------------------------------- |
| Framework        | Spring Boot                                                      |
| Mapping          | DTOs are manually mapped in `mapper/`                            |
| Authentication   | JWT-based; passwords are hashed                                  |
| Validation       | Enforced via `@Valid` and annotations                            |
| API Docs         | OpenAPI/Swagger used (`@Schema` present)                         |
| File Handling    | Currently file paths stored in DB; planning for filesystem/cloud |
| DTO Usage        | Used in both controller and service layers                       |
| Error Structure  | Unified via `ErrorResponseDTO`                                   |
| Layer Separation | DTOs are never used as persistence entities                      |

---

## 💡 Notes for Contributors

* Always create a separate DTO for API input/output instead of using entities directly.
* If adding time fields, document UTC usage and validate client behavior.
* Extend `ErrorResponseDTO` to support field-level validation errors if needed.

---