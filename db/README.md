# 💼 JobTrackr – Advanced Job Application Tracking System

**JobTrackr** is a full-featured, backend-first job application tracker designed to help users manage every aspect of their job search — from applications and resumes to recruiter communication, timelines, and follow-up reminders.

This system goes beyond CRUD, modeling the real-world complexity of job searching, and is built with enterprise-level architecture in mind.

---

## 📌 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [System Overview](#system-overview)
- [Data Model Summary](#data-model-summary)
- [Design Principles](#design-principles)
- [Future Plans](#future-plans)

---

## 🌟 Features

- ✅ Secure user authentication (JWT-based)
- ✅ Track job applications with detailed metadata
- ✅ Manage resume versions and cover letters
- ✅ Timeline of application events
- ✅ Log all recruiter communication
- ✅ Attach offer letters, prep notes, rejection letters, job descriptions, etc.
- ✅ Set reminders for follow-ups
- ✅ Schedule interviews and assessments
- ✅ Audit log for all major actions
- ✅ Normalize and structure sources, skills, and locations

---

## ⚙️ Tech Stack

| Layer       | Tech                         |
|-------------|------------------------------|
| Backend     | Spring Boot (Java)           |
| Database    | PostgreSQL                   |
| ORM         | Spring Data JPA + Hibernate  |
| Security    | Spring Security + JWT        |
| Build Tool  | Maven                        |
| Docs        | Swagger / OpenAPI (planned)  |
| Deployment  | Render / Heroku (planned)    |

---

## 🧠 System Overview

JobTrackr is designed for real users to log:
- Applications to roles (title, company, location, source, resume used)
- Lifecycle updates like interviews or rejections
- Communications like calls, emails, and LinkedIn DMs
- Scheduled events like interviews or assessments
- Reminders and follow-ups
- Supporting files like resumes, offer letters, and job descriptions

The system uses normalized models to avoid redundancy and enhance flexibility.

---

## 🗃️ Data Model Summary

### 👤 `User`
Handles authentication and owns all resources.

---

### 💼 `JobApplication`
Central entity representing a job applied to. Contains title, company, status, source, location, notes, etc.

- `status` is an enum: `APPLIED`, `INTERVIEW`, `OFFER`, `REJECTED`
- Linked to `Location`, `JobSource`, `Resume`, and `CoverLetter`

---

### 🧾 `Resume`
Stores different resume versions with:
- `file_path` (only)
- No `user_id` or direct job link; can be reused across jobs

---

### 📝 `CoverLetter`
Stores general or reusable letters:
- Contains title and file path or content
- Not tied to specific job application (like `Resume`)

---

### 📅 `ApplicationTimeline`
Tracks key lifecycle events like:
- Application created, Interview scheduled, Offer made
- Enum `event_type`: `CREATED`, `FOLLOW_UP`, `INTERVIEW`, `REJECTED`, `OFFER`, `NOTE`

---

### 📨 `CommunicationLog`
Logs recruiter interactions:
- Enum `type`: `EMAIL`, `CALL`, `LINKEDIN`, `IN_PERSON`
- Enum `direction`: `INBOUND`, `OUTBOUND`

---

### 🔔 `FollowUpReminder`
Sets reminders for follow-up actions with `remind_on` timestamp.

---

### 📆 `ScheduledCommunication`
Logs upcoming events users must attend:
- Interviews, assessments, HR calls, etc.
- Type enum: `INTERVIEW`, `ONLINE_ASSESSMENT`, `CALL`

---

### 📎 `Attachment`
Single model for all uploaded files:
- Job descriptions (`type: JOB_DESCRIPTION`)
- Offer letters (`type: OFFER_LETTER`)
- Interview prep, rejections, etc.
- Linked to a job via `job_application_id`

---

### 🧪 `AuditLog`
Tracks changes across the system:
- Enum `action`: `CREATE`, `UPDATE`, `DELETE`
- `description` is **required** to explain each change

---

### 🗺️ `Location`
Normalized city and country for analytics and filtering.

---

### 🧠 `SkillTracker`
Links required skills to each job (future AI-ready).

---

### 🌐 `JobSource`
Normalized table for application source (LinkedIn, Indeed, Referral, etc.)

---

## ✅ Design Principles

- **Normalization**: Data is cleanly separated (e.g., `Location`, `JobSource`)
- **Minimal Redundancy**: `Resume` and `CoverLetter` aren't tied directly to applications
- **Auditability**: All changes are tracked through `AuditLog`
- **Extendability**: Supports future AI integration (e.g., skill analysis, job matching)
- **Security-first**: All routes gated by JWT auth and user ownership checks

---

## 🔮 Future Plans

- Analytics dashboard (conversion rates, source performance)
- Export data (PDF, CSV)
- AI: Resume-job matching, follow-up email generator
- Calendar view for events and reminders
- REST → GraphQL (optional phase)
- Frontend with React

---

## 📂 Folder Structure (Backend)

TBC

---

## 👋 Want to Contribute?

This project will soon be made public for open-source contributions. Stay tuned for the contribution guide!

---


