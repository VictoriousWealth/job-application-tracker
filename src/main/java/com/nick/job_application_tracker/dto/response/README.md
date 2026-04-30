# Response DTOs

This package contains lightweight response models used for list endpoints, create responses, and computed read-only views such as analytics and exports.

## Contents

- response DTOs for the main business entities such as job applications, resumes, cover letters, reminders, communications, and metadata
- computed response DTOs for dashboard summaries, analytics, recommendations, calendar events, matching results, and workspace exports

## Responsibility

These classes provide stable client-facing shapes for common reads without requiring every response to use the heavier detail models.

## Current State Note

This package is the default outbound DTO family for collection endpoints.
