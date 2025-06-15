# 🔄 Mappers - JobTrackr

This directory contains all **Mapper classes** used to convert between internal **JPA entities** (in the `model/` package) and **Data Transfer Objects (DTOs)** (in the `dto/` package) within the JobTrackr Spring Boot application.

Mappers are responsible for:
- Transforming incoming client data (`DTO → Entity`) for persistence or processing.
- Preparing internal data (`Entity → DTO`) for safe and structured API responses.

They play a critical role in **decoupling** the persistence layer from the API layer and ensure proper data validation, transformation, and security enforcement.

---

## 📁 Mapper Index (Grouped by Domain)

### 📌 Application Lifecycle

- [`ApplicationTimelineMapper.java`](./ApplicationTimelineMapper.java)  
  Maps `ApplicationTimeline` ↔ `ApplicationTimelineDTO`.  
  Handles enum parsing (`eventType`) and links to `JobApplication` by ID only.

- [`AuditLogMapper.java`](./AuditLogMapper.java)  
  Maps `AuditLog` ↔ `AuditLogDTO`.  
  Assigns the current authenticated user and sets `createdAt` internally (not client-controlled).

---

### 📬 Communication & Scheduling

- [`CommunicationLogMapper.java`](./CommunicationLogMapper.java)  
  Maps `CommunicationLog` ↔ `CommunicationLogDTO`.  
  Validates enums (`direction`, `type`) and applies timestamp defaults if needed.

- [`ScheduledCommunicationMapper.java`](./ScheduledCommunicationMapper.java)  
  Maps `ScheduledCommunication` ↔ `ScheduledCommunicationDTO`.  
  Used for scheduling interviews, assessments, or calls.

---

### 📄 Application Data

- [`JobApplicationMapper.java`](./JobApplicationMapper.java)  
  Maps between:
  - `JobApplicationCreateDTO` → `JobApplication`
  - `JobApplication` → `JobApplicationResponseDTO` (for frontend)
  - `JobApplication` → `JobApplicationDetailDTO` (for internal use)  
  Resolves optional relationships like `Resume`, `CoverLetter`, `Source`, and `Location`.

- [`FollowUpReminderMapper.java`](./FollowUpReminderMapper.java)  
  Maps `FollowUpReminderCreateDTO` → `FollowUpReminder`  
  and `FollowUpReminder` → `FollowUpReminderDTO`, associating reminders to job applications.

---

### 📎 Attachments & Supporting Docs

- [`AttachmentMapper.java`](./AttachmentMapper.java)  
  Maps `Attachment` ↔ `AttachmentDTO`, including file path and type enums.

- [`ResumeMapper.java`](./ResumeMapper.java)  
  Maps `Resume` ↔ `ResumeDTO`.  
  Currently handles basic file path mapping.

- [`CoverLetterMapper.java`](./CoverLetterMapper.java)  
  Maps `CoverLetter` ↔ `CoverLetterDTO`, handling both raw text and file path storage.

---

### 🧠 Skills & Sources

- [`SkillTrackerMapper.java`](./SkillTrackerMapper.java)  
  Maps between `SkillTrackerCreateDTO`, `SkillTrackerDTO`, and `SkillTracker` entity.

- [`JobSourceMapper.java`](./JobSourceMapper.java)  
  Maps `JobSourceCreateDTO` ↔ `JobSource`  
  and `JobSource` ↔ `JobSourceDTO`.

---

### 🌍 Location & User

- [`LocationMapper.java`](./LocationMapper.java)  
  Maps `Location` ↔ `LocationDTO`.  
  Also includes an `updateEntity()` method for partial updates.

- [`UserMapper.java`](./UserMapper.java)  
  Converts a `User` → `UserInfoDTO`, transforming assigned roles into a `Set<String>`.

---

## ⚙️ Design Considerations

| Concern             | Approach                                                                 |
|---------------------|--------------------------------------------------------------------------|
| **Enum Parsing**     | All enums are parsed using `Enum.valueOf()` to match DB constraints.     |
| **Foreign Keys**     | Relationships are handled by setting IDs only — no eager fetching inside mappers. |
| **Timestamps**       | Fields like `createdAt`, `timestamp`, `remindOn` are system-controlled (not client-supplied). |
| **Spring Integration** | Mappers are annotated with `@Component` (unless only static methods are used). |
| **Null Safety**       | Nullable fields (like resume, cover letter, location) are handled safely in DTO construction. |

---

## 🔐 Security Best Practices

- Mappers never expose sensitive internal data like passwords or internal IDs unless explicitly required.
- Server-controlled fields (e.g. timestamps, user identity) are set in the backend — never overwritten by client input.
- Enum validation helps prevent injection or broken logic via invalid values.

---

## 🛠 Contributor Notes

If you're contributing new features, follow these practices:

1. **Each new Entity ↔ DTO pair requires a mapper.**
2. Place **no business logic** in mappers — limit to transformation only.
3. Use `Enum.valueOf()` cautiously — wrap in try-catch if receiving untrusted input.
4. Use `@Component` if dependency injection is needed in services.
5. Name methods clearly: `toDTO()`, `toEntity()`, and `updateEntity()` if applicable.

---

## 🧾 Example Patterns

```java
// Enum-safe conversion from DTO → Entity
entity.setType(MyEnum.valueOf(dto.getType()));

// Partial foreign key association
JobApplication app = new JobApplication();
app.setId(dto.getJobApplicationId());
entity.setJobApplication(app);
````

---

## 🧩 Schema Alignment

This mapper layer aligns with the PostgreSQL schema:

* Enum types (`status`, `type`, `direction`, `eventType`, etc.) are checked via DB constraints.
* Foreign key relationships match the structure defined in `schema.sql`.
* Relationships are represented as `id` references, not full sub-entities, in DTOs.

---

Let me know if you'd like this saved as a file (`mapper/README.md`) or want a matching `commit message` suggestion for this and the DTO readme together.

---
