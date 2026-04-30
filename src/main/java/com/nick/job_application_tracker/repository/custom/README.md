# Custom Repository Contracts

This package contains custom repository fragment interfaces for queries that go beyond simple derived Spring Data methods.

## Contents

- `*RepositoryCustom` interfaces for applications, documents, communications, reminders, schedules, skills, users, sources, locations, timelines, and audit logs

## Responsibility

These interfaces define advanced query capabilities, usually for filtering, projections, or domain-specific lookup behavior that is awkward to express as plain repository method names.

## Current State Note

Some fragments reflect older repository structure. Keep them aligned with the active repositories under `inter_face` and remove unused fragments when they are no longer wired into live code.
