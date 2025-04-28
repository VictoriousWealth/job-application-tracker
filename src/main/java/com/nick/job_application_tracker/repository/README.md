# Repositories

This directory contains the Spring Data JPA repositories for the JobTrackr application.  
Repositories abstract and simplify database access by providing ready-made CRUD operations.

---

## Repositories:

- [ApplicationTimelineRepository.java](./ApplicationTimelineRepository.java)  
  Manages timeline events related to job applications.

- [AttachmentRepository.java](./AttachmentRepository.java)  
  Handles file attachments linked to job applications.

- [AuditLogRepository.java](./AuditLogRepository.java)  
  Provides access to audit log entries capturing system actions.

- [CommunicationLogRepository.java](./CommunicationLogRepository.java)  
  Retrieves communication logs (email, call, LinkedIn, in-person) for job applications.

- [CoverLetterRepository.java](./CoverLetterRepository.java)  
  Manages storage and retrieval of user cover letters.

- [FollowUpReminderRepository.java](./FollowUpReminderRepository.java)  
  Manages reminders for users to follow up on applications.

- [JobApplicationRepository.java](./JobApplicationRepository.java)  
  Provides CRUD operations and user-specific filtering for job applications.

- [JobSourceRepository.java](./JobSourceRepository.java)  
  Manages the job sources such as LinkedIn, Indeed, or referrals.

- [LocationRepository.java](./LocationRepository.java)  
  Handles city and country information associated with job applications.

- [ResumeRepository.java](./ResumeRepository.java)  
  Manages user-uploaded resumes.

- [ScheduledCommunicationRepository.java](./ScheduledCommunicationRepository.java)  
  Manages scheduled events such as interviews and online assessments.

- [SkillTrackerRepository.java](./SkillTrackerRepository.java)  
  Associates tracked skills with job applications.

- [UserRepository.java](./UserRepository.java)  
  Manages user data and authentication-related queries (e.g., find by email).

---

## Notes:
- All repositories extend `JpaRepository`, providing basic CRUD functionality.
- Some repositories define **custom finder methods** like `findByJobApplicationId`, `findByUserId`, or `findByEmail` for optimized queries.

