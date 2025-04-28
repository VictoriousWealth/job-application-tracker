# 💼 JobTrackr – Full-Stack Job Application Tracker

[![Coverage Checker](https://img.shields.io/badge/Test%20Coverage-Available-green?style=flat-square)](./scripts/README.md)
[![Gradle Tests](https://img.shields.io/badge/Gradle%20Tests-Supported-blue?style=flat-square)](https://gradle.org/)
[![Built with Java](https://img.shields.io/badge/Built%20with-Java%2017-blueviolet?style=flat-square)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](./LICENSE)

**JobTrackr** is a comprehensive system designed to streamline and manage every part of the job search process — tracking applications, managing resumes and cover letters, logging communication, setting follow-ups, and organizing timelines.  
It is built for real-world use with a secure backend, a clean modular design, and future-ready architecture.

---

## 📌 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [System Overview](#system-overview)
- [Testing and Code Quality](#testing-and-code-quality)
- [Scripts](#scripts)
- [Folder Structure](#folder-structure)
- [Future Plans](#future-plans)

---

## 🌟 Features

- ✅ Secure JWT-based user authentication
- ✅ Advanced job application tracking
- ✅ Resume and cover letter management
- ✅ Timeline and event tracking
- ✅ Recruiter communication logs
- ✅ Reminder and notification system
- ✅ Audit logging for all key actions
- ✅ Attach important documents (offers, rejections, prep material)
- ✅ Structured data models for sources, locations, and skills
- ✅ Extendable and AI-ready backend

---

## ⚙️ Tech Stack

| Layer         | Technology                        |
|---------------|------------------------------------|
| Backend       | Spring Boot (Java)                |
| Database      | PostgreSQL                        |
| ORM           | Spring Data JPA + Hibernate        |
| Security      | Spring Security + JWT             |
| Build Tool    | Gradle                            |
| Docs          | Swagger/OpenAPI (Planned)         |
| Deployment    | Render / Heroku (Planned)         |

---

## 🧠 System Overview

JobTrackr models the real-world complexity of job hunting:
- Track applications to roles with detailed metadata
- Manage communications across multiple channels (email, calls, LinkedIn)
- Schedule interviews and assessments
- Attach and organize supporting files
- Normalize locations, sources, and skills for analytics and filtering
- Log all significant actions for transparency and auditability

Built with an enterprise architecture mindset, prioritizing **security**, **extendability**, and **real usability**.

---

## 🧪 Testing and Code Quality

Testing is built into the development lifecycle:
- **Per-class test coverage** is tracked with a custom script.
- **Gradle integration** to run tests by module or globally.
- **Test Coverage Metrics** are output with colored progress bars.
- **Auditability**: Changes are logged through the `AuditLog` model.

### 🛠️ Check Test Coverage
Run the custom coverage checker:

```bash
./scripts/check_test_coverage.sh
```

Options:
- `--show-missing` or `--s-m` → Also show classes without corresponding tests.
- `--with-gradle` → Automatically run Gradle tests for each major package/folder.
- `-h` or `--help` → Show usage information.

Example:

```bash
./scripts/check_test_coverage.sh --show-missing --with-gradle
```

---

Perfect, you already wrote a very nice script README.  
Let's **link it cleanly** from the general `README.md` so recruiters and engineers easily discover it.

Here’s how we can **update the Scripts section** of the general README:

---

## 🧹 Scripts

| Script | Description |
|:------|:------------|
| [`scripts/check_test_coverage.sh`](./scripts/README.md) | Calculates Java class test coverage. Shows per-folder breakdowns. Integrates with Gradle test runner. |

---

## 📂 Folder Structure (Backend)

TBC (To be completed once frontend and backend stabilization is done)

---

## 🔮 Future Plans

- Analytics dashboard (conversion rates, offers vs rejections)
- Calendar-based event management
- Resume and Job Matching AI (Skill-based scoring)
- Export capabilities (CSV, PDF)
- GraphQL migration option
- Full frontend client (React)

---

## 👋 Want to Contribute?

The project will soon open for external contributions. Stay tuned for the contribution guide!

---

# 🚀 Let's make job tracking seamless!

---
