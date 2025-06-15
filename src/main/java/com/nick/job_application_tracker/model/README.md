# 🧬 Model Package - `com.nick.job_application_tracker.model`

This package defines the **core JPA entities** for the JobTrackr application. These map directly to PostgreSQL tables and represent the persistent domain model for job tracking, user management, communication logs, auditing, and scheduling.

---

## 🗂 Quick Entity Index

- [`ApplicationTimeline`](./ApplicationTimeline.java)
- [`Attachment`](./Attachment.java)
- [`AuditLog`](./AuditLog.java)
- [`CommunicationLog`](./CommunicationLog.java)
- [`CoverLetter`](./CoverLetter.java)
- [`FollowUpReminder`](./FollowUpReminder.java)
- [`JobApplication`](./JobApplication.java)
- [`JobSource`](./JobSource.java)
- [`Location`](./Location.java)
- [`Resume`](./Resume.java)
- [`ScheduledCommunication`](./ScheduledCommunication.java)
- [`SkillTracker`](./SkillTracker.java)
- [`User`](./User.java)
- [`Role`](./Role.java)

---

## 📦 Entity Breakdown

### 🔐 User & Authentication

- **`User`** — Authenticated system user  
  Fields: `email`, `password`, `role`, `isEnabled`  
  Related to: `JobApplication`, `AuditLog`

- **`Role` (enum)** — User role  
  Value: `BASIC`

---

### 💼 Core Application Structure

- **`JobApplication`** — Central record of a job prospect  
  Fields: `jobTitle`, `company`, `status`, `appliedOn`, `notes`  
  Enums: `Status`: `APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`  
  Relations:
  - Required: `User`
  - Optional: `Location`, `JobSource`, `Resume`, `CoverLetter`
  - One-to-many: `SkillTracker`, `ApplicationTimeline`, `CommunicationLog`, `FollowUpReminder`, `ScheduledCommunication`, `Attachment`

---

### 🗂 Supporting Tables

- **`Location`** — Geographic metadata  
  Fields: `city`, `country`

- **`JobSource`** — Origin of the opportunity  
  Fields: `name`

- **`Resume`** — User-uploaded resume  
  Fields: `filePath`

- **`CoverLetter`** — Custom letter  
  Fields: `title`, `filePath`, `content` (as `@Lob`)

---

### 📎 Add-ons & Trackers

- **`SkillTracker`** — Tracks job-specific skills  
  Fields: `skillName`

- **`Attachment`** — Uploaded docs tied to an app  
  Fields: `filePath`, `type`  
  Enum: `Type`: `JOB_DESCRIPTION`, `OFFER_LETTER`, `INTERVIEW_PREP`, `REJECTION_LETTER`, `OTHER`

---

### 📞 Logs & Communication

- **`CommunicationLog`** — Email/call/LinkedIn tracking  
  Fields: `type`, `direction`, `timestamp`, `message`  
  Enums:
  - `Method`: `EMAIL`, `CALL`, `LINKEDIN`, `IN_PERSON`
  - `Direction`: `INBOUND`, `OUTBOUND`

- **`ScheduledCommunication`** — Future interactions  
  Fields: `type`, `scheduledFor`, `notes`  
  Enum: `Type`: `INTERVIEW`, `ONLINE_ASSESSMENT`, `CALL`

---

### 🕒 Timeline & Audit

- **`ApplicationTimeline`** — Lifecycle events for an application  
  Fields: `eventType`, `eventTime`, `description`  
  Enum: `EventType`: `CREATED`, `UPDATED`, `SUBMITTED`, `CANCELLED`

- **`FollowUpReminder`** — Reminder for user actions  
  Fields: `remindOn`, `note`

- **`AuditLog`** — System auditing  
  Fields: `action`, `description`, `createdAt`, `performedBy`  
  Enum: `Action`: `CREATE`, `UPDATE`, `DELETE`

---

## 🧠 Enum Glossary

| Enum Location                 | Values                                              |
|------------------------------|-----------------------------------------------------|
| `Role`                       | `BASIC`                                             |
| `JobApplication.Status`      | `APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`         |
| `ApplicationTimeline.Type`   | `CREATED`, `UPDATED`, `SUBMITTED`, `CANCELLED`      |
| `AuditLog.Action`            | `CREATE`, `UPDATE`, `DELETE`                        |
| `CommunicationLog.Method`    | `EMAIL`, `CALL`, `LINKEDIN`, `IN_PERSON`            |
| `CommunicationLog.Direction` | `INBOUND`, `OUTBOUND`                               |
| `Attachment.Type`            | `JOB_DESCRIPTION`, `OFFER_LETTER`, etc.             |
| `ScheduledCommunication.Type`| `INTERVIEW`, `ONLINE_ASSESSMENT`, `CALL`            |

_All enums are stored in the database using `EnumType.STRING`._

---

## 🌐 Timezone Policy

All `LocalDateTime` fields are stored and interpreted as **UTC**. This includes:
- `appliedOn`
- `eventTime`
- `timestamp`
- `remindOn`
- `scheduledFor`
- `createdAt`

---

## 🗺️ Database Schema Diagram

The following diagram illustrates the entity relationships and foreign key structure:

📷 [`job_tracker_schema_graph.png`](./job_tracker_schema_graph.png)

---

## 📄 Full Schema SQL Dump

See the PostgreSQL schema for all table definitions and enum types:

📄 [`schema.sql`](./schema.sql)

---

## 🧠 Suggestions for Future Enhancements

- [ ] Add cardinality labels to ERD (e.g., `1 → *`)
- [ ] Add Swagger enum documentation
- [ ] Include test data and entity seeders
- [ ] Provide `pg_dump` or Hibernate instructions for regenerating schema

---

## 🛠 Developer Notes

- Use `@ManyToOne(optional = false)` for required FK constraints.
- Enforce constraints using `@NotNull` and DB schema.
- Sensitive fields (e.g., passwords) are never exposed in DTOs.
- Enum string values must match exactly with DB definitions.
- Time fields like `createdAt` and `eventTime` are always server-generated.

