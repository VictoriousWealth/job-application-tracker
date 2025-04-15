# 🗃️ Database Models – JobTrackr

This directory contains the data model definitions and entity relationships used by the JobTrackr backend system (Spring Boot + PostgreSQL).

---

## 📐 ER Diagram (Simplified)

User (1) ──── (∞) JobApplication

---

## 🧑‍💼 User

Represents an individual user with access to the system.  
All job applications are scoped to a specific user.

| Field        | Type     | Description                      |
|--------------|----------|----------------------------------|
| `id`         | UUID     | Primary Key                      |
| `email`      | String   | Unique user email (used to log in) |
| `password`   | String   | Encrypted password (BCrypt)      |
| `full_name`  | String   | User's full name                 |
| `created_at` | Timestamp| Date of account creation         |

---

## 💼 JobApplication

Represents a single job opportunity the user has applied for.

| Field              | Type      | Description                                  |
|--------------------|-----------|----------------------------------------------|
| `id`               | UUID      | Primary Key                                  |
| `user_id`          | UUID      | Foreign Key – references `User.id`           |
| `job_title`        | String    | Title of the position                        |
| `company_name`     | String    | Name of the company                          |
| `location`         | String    | Location (or city) of the job                |
| `is_remote`        | Boolean   | Whether it's a remote position               |
| `application_date` | Date      | Date the job was applied to                  |
| `status`           | Enum      | Current status: `APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED` |
| `source`           | String    | Where the user found the job (LinkedIn, etc)|
| `resume_version`   | String    | Which resume version was used (optional)     |
| `notes`            | Text      | Free-form notes about the application        |
| `created_at`       | Timestamp | Created timestamp                            |
| `updated_at`       | Timestamp | Last updated timestamp                       |

---

## 🧾 Notes

- Timestamps are handled automatically using `@CreationTimestamp` and `@UpdateTimestamp`.
- `status` is stored as an `Enum` in Java but saved as a `String` in the DB for readability.
- Additional tables (e.g., `Resume`, `Company`, `InterviewLog`) may be added as stretch features.

---

## 📚 Future Model Ideas

- **Resume** (track uploaded versions or templates)
- **Interview** (date, round, feedback)
- **JobOffer** (salary, benefits, deadline)
- **ApplicationTimeline** (events like "Follow-up email sent")

---

> 📌 For schema evolution, we recommend using Liquibase or Flyway in `src/main/resources/db/migration`.

