# Mappers

This directory contains all mappers used in the JobTrackr application.  
Mappers convert between entity models and Data Transfer Objects (DTOs), ensuring that internal persistence models are separated from external API-facing models.

---

## Timeline, Communication, and Audit Mappers:

- [ApplicationTimelineMapper.java](./ApplicationTimelineMapper.java) — Maps between `ApplicationTimeline` entity and `ApplicationTimelineDTO`.
- [CommunicationLogMapper.java](./CommunicationLogMapper.java) — Maps communication logs between entity and DTO.
- [AuditLogMapper.java](./AuditLogMapper.java) — Maps audit logs between entity and DTO.

---

## Application and Follow-Up Mappers:

- [JobApplicationMapper.java](./JobApplicationMapper.java) — Maps job applications to detailed or summary DTOs.
- [FollowUpReminderMapper.java](./FollowUpReminderMapper.java) — Maps follow-up reminders for scheduled job application activities.

---

## Attachments, Cover Letters, Resumes:

- [AttachmentMapper.java](./AttachmentMapper.java) — Maps file attachments linked to job applications.
- [CoverLetterMapper.java](./CoverLetterMapper.java) — Maps cover letter content between entity and DTO.
- [ResumeMapper.java](./ResumeMapper.java) — Maps resumes (file paths) between entity and DTO.

---

## Scheduling Mappers:

- [ScheduledCommunicationMapper.java](./ScheduledCommunicationMapper.java) — Maps interviews, assessments, and calls to scheduled communications DTOs.

---

## Skills and Sources:

- [SkillTrackerMapper.java](./SkillTrackerMapper.java) — Maps skills associated with job applications.
- [JobSourceMapper.java](./JobSourceMapper.java) — Maps job source data (LinkedIn, Indeed, etc.).

---

## Location and User Mappers:

- [LocationMapper.java](./LocationMapper.java) — Maps city and country information for job applications.
- [UserMapper.java](./UserMapper.java) — Maps user profiles and their roles into a user-facing DTO.

---

## Notes:
- All mappers ensure that only relevant and safe data is exposed via API endpoints.
- `@Component` is used to register mappers as Spring-managed beans where needed.
- Some mappers use `static` methods when dependency injection is not required.

