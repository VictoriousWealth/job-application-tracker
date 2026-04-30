# Update DTOs

This package contains request payloads used when clients modify existing records.

## Contents

- `*UpdateDTO` classes for applications, resumes, cover letters, attachments, reminders, schedules, communications, sources, locations, timelines, and skills

## Responsibility

Update DTOs define the mutable fields accepted by the API. Path parameters and authenticated user context should supply record identity and ownership, not the request body itself.

## Current State Note

These DTOs should stay aligned with validation rules and allowed business-state transitions in the service layer.
