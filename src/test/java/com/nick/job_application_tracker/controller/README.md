# Controller Tests

This package contains endpoint-level tests for the REST API controllers.

## Contents

- controller-specific tests for auth, users, job applications, documents, reminders, schedules, audit logs, sources, locations, skills, attachments, communications, and timelines

## Responsibility

Controller tests should verify routing, HTTP status codes, request validation, authentication and authorization behavior, and response-shape correctness.

## Current State Note

These tests are most useful when they stay behavior-focused and avoid duplicating service-layer implementation details.
