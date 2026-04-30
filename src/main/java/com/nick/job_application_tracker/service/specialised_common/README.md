# Specialized Service Contracts

This package contains domain-specific service interfaces for the main business areas.

## Contents

- `*ServiceInterface` contracts for applications, documents, reminders, schedules, skills, users, sources, locations, timelines, communications, and audit logs

## Responsibility

These interfaces describe domain-level capabilities more precisely than the generic service contract in `service.common`.

## Current State Note

The package name reflects an older naming convention. Keep these interfaces aligned with the concrete implementations and remove redundant contracts if they stop adding value.
