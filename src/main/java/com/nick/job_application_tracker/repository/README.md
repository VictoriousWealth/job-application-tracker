# 🗃️ Repositories — `com.nick.job_application_tracker.repository`

This package contains all Spring Data JPA repositories used in the **JobTrackr** application.  
Repositories provide out-of-the-box CRUD operations and are used in service classes to abstract database interactions.

---

## 📦 Repository Overview

| Repository                              | Description                                                                 |
|-----------------------------------------|-----------------------------------------------------------------------------|
| [`ApplicationTimelineRepository`](./ApplicationTimelineRepository.java)         | Manages timeline events related to job applications.                        |
| [`AttachmentRepository`](./AttachmentRepository.java)                           | Handles file attachments linked to job applications.                        |
| [`AuditLogRepository`](./AuditLogRepository.java)                               | Provides access to audit logs for user/system activity.                     |
| [`CommunicationLogRepository`](./CommunicationLogRepository.java)               | Retrieves communication entries (email, call, LinkedIn, etc.).              |
| [`CoverLetterRepository`](./CoverLetterRepository.java)                         | Stores user-generated or uploaded cover letters.                            |
| [`FollowUpReminderRepository`](./FollowUpReminderRepository.java)               | Manages follow-up reminders for job applications.                           |
| [`JobApplicationRepository`](./JobApplicationRepository.java)                   | Core job application repository with support for user/status filtering.     |
| [`JobSourceRepository`](./JobSourceRepository.java)                             | Manages job sources like LinkedIn, Indeed, or referrals.                    |
| [`LocationRepository`](./LocationRepository.java)                               | Resolves and reuses city-country location combinations.                     |
| [`ResumeRepository`](./ResumeRepository.java)                                   | Stores resume file references.                                              |
| [`ScheduledCommunicationRepository`](./ScheduledCommunicationRepository.java)   | Manages interviews, calls, and other upcoming scheduled events.             |
| [`SkillTrackerRepository`](./SkillTrackerRepository.java)                       | Tracks skills associated with job applications.                             |
| [`UserRepository`](./UserRepository.java)                                       | Retrieves users, including authentication-based queries (e.g., by email).   |

---

## 🔍 Custom Query Methods

Many repositories extend standard `JpaRepository<T, ID>` functionality with **custom finders**, including:

```java
List<JobApplication> findByUserId(Long userId);
Optional<User> findByEmail(String email);
List<Attachment> findByJobApplicationId(Long jobId);
Optional<Location> findByCityAndCountry(String city, String country);
````

These methods follow Spring Data’s **method name conventions** and are automatically translated into SQL queries.

---

## 🧠 Notes

* All repositories are annotated with `@Repository` and picked up automatically by Spring Boot.
* Each repository is tightly coupled with a model/entity in [`model/`](../model/README.md).
* Custom queries can be expanded with:

  * `@Query` annotations (JPQL or native SQL)
  * Specifications (`JpaSpecificationExecutor`), if needed
* Query results like `Optional<T>` or `List<T>` help ensure null-safe, expressive interfaces.

---

## 🧪 Repository Testing

Recommended tools for testing repository behavior:

* `@DataJpaTest` with embedded H2 or TestContainers
* `TestEntityManager` for setup and assertions
* Mocking repositories only for service-level tests

---

## 📚 See Also

* [`model/`](../model/README.md) — The entities that these repositories persist
* [`service/`](../service/) — Where most repositories are injected and used
* [Spring Data JPA Docs](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)

---
