# Models

This directory contains the core data entities (JPA entities) used in the JobTrackr application.  
Each model maps to a corresponding database table and represents critical aspects of job tracking, user management, and audit logging.

---

## Entities:

- [ApplicationTimeline.java](./ApplicationTimeline.java)  
  Tracks major events (created, updated, submitted, cancelled) related to a job application.

- [Attachment.java](./Attachment.java)  
  Stores supplementary documents linked to applications, like job descriptions, offer letters, and more.

- [AuditLog.java](./AuditLog.java)  
  Records key system actions (create, update, delete) along with the user responsible for the action.

- [CommunicationLog.java](./CommunicationLog.java)  
  Captures communication events related to job applications (email, call, LinkedIn, in-person).

- [CoverLetter.java](./CoverLetter.java)  
  Represents user cover letters, either as uploaded files or rich text content.

- [FollowUpReminder.java](./FollowUpReminder.java)  
  Sets scheduled reminders for users to follow up on specific job applications.

- [JobApplication.java](./JobApplication.java)  
  Central entity for tracking a user's job applications, including company, title, status, resume, and notes.

- [JobSource.java](./JobSource.java)  
  Identifies the platform or method through which the job opportunity was found (e.g., LinkedIn, Referral).

- [Location.java](./Location.java)  
  Stores city and country information associated with a job listing.

- [Resume.java](./Resume.java)  
  Represents user-uploaded resumes, stored as file paths.

- [ScheduledCommunication.java](./ScheduledCommunication.java)  
  Tracks upcoming important events like interviews, online assessments, and scheduled calls.

- [SkillTracker.java](./SkillTracker.java)  
  Associates key skills with each job application to monitor required qualifications.

- [User.java](./User.java)  
  Represents a JobTrackr user, including email, password, role, and enabled status.

---

## Enums:

- [Role.java](./Role.java)  
  Defines user roles within the system (currently only `BASIC`).

- **Other Embedded Enums** (inside models):  
  - `ApplicationTimeline.EventType`
  - `Attachment.Type`
  - `AuditLog.Action`
  - `CommunicationLog.Method` and `Direction`
  - `JobApplication.Status`
  - `ScheduledCommunication.Type`

---

Each entity uses JPA and Hibernate annotations to map to relational database tables.  
The models are designed for clean relationships (`@ManyToOne`, `@Enumerated`) and extensibility as the system grows.
