# Repository Tests

This package contains persistence-focused tests for the repository layer.

## Contents

- repository tests for applications, resumes, cover letters, attachments, communications, reminders, schedules, skills, users, sources, locations, timelines, and audit logs

## Responsibility

Repository tests should verify query correctness, UUID lookups, ownership scoping, soft-delete behavior, and custom finder semantics.

## Current State Note

These tests become especially important when derived query names or custom repository fragments change.
