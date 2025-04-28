# Data Transfer Objects (DTOs)

This directory contains all Data Transfer Objects (DTOs) used in the JobTrackr application.  
DTOs are simple objects used to transfer data between layers (e.g., between the backend and frontend) without exposing internal entities.

---

## Application and Timeline DTOs:

- [ApplicationTimelineDTO.java](./ApplicationTimelineDTO.java) — Represents timeline events related to job applications.
- [AuditLogDTO.java](./AuditLogDTO.java) — Represents audit log entries for user actions.

---

## Attachments, Resumes, Cover Letters:

- [AttachmentDTO.java](./AttachmentDTO.java) — Represents an attachment linked to a job application.
- [ResumeDTO.java](./ResumeDTO.java) — Represents a user-uploaded resume.
- [CoverLetterDTO.java](./CoverLetterDTO.java) — Represents a cover letter (title, path, content).

---

## Job Application DTOs:

- [JobApplicationCreateDTO.java](./JobApplicationCreateDTO.java) — DTO for creating new job applications.
- [JobApplicationDetailDTO.java](./JobApplicationDetailDTO.java) — Detailed view of a job application.
- [JobApplicationResponseDTO.java](./JobApplicationResponseDTO.java) — Response DTO for job applications including readable fields.
- [FollowUpReminderCreateDTO.java](./FollowUpReminderCreateDTO.java) — Used for creating follow-up reminders.
- [FollowUpReminderDTO.java](./FollowUpReminderDTO.java) — Represents a follow-up reminder.

---

## Communication DTOs:

- [CommunicationLogDTO.java](./CommunicationLogDTO.java) — Communication history (email, call, etc.) for job applications.
- [ScheduledCommunicationCreateDTO.java](./ScheduledCommunicationCreateDTO.java) — DTO for scheduling communications like interviews.
- [ScheduledCommunicationDTO.java](./ScheduledCommunicationDTO.java) — Represents scheduled communications tied to a job application.

---

## Skills and Sources:

- [SkillTrackerCreateDTO.java](./SkillTrackerCreateDTO.java) — DTO for associating skills with a job application.
- [SkillTrackerDTO.java](./SkillTrackerDTO.java) — Represents a skill associated with a job application.
- [JobSourceCreateDTO.java](./JobSourceCreateDTO.java) — DTO for creating job sources.
- [JobSourceDTO.java](./JobSourceDTO.java) — Represents a job source (e.g., LinkedIn, Referral).

---

## User and Authentication DTOs:

- [LoginRequest.java](./LoginRequest.java) — Payload for user login.
- [SignupRequest.java](./SignupRequest.java) — Payload for user signup.
- [JwtResponse.java](./JwtResponse.java) — JWT token returned after successful authentication.
- [UserInfoDTO.java](./UserInfoDTO.java) — Represents basic user profile information.
- [UserUpdateDTO.java](./UserUpdateDTO.java) — DTO for updating user details.

---

## Miscellaneous:

- [LocationDTO.java](./LocationDTO.java) — Represents a city and country associated with an application.
- [ErrorResponseDTO.java](./ErrorResponseDTO.java) — Standard error response format for API errors.

---

## Notes:
- DTOs separate external data communication from internal model logic, improving security and flexibility.
- Most DTOs match 1-to-1 with entities but selectively expose necessary fields.
