# Mapper Tests

This package contains tests for DTO and entity mapping behavior.

## Contents

- mapper tests for applications, documents, reminders, schedules, sources, locations, skills, communications, timelines, users, and audit logs

## Responsibility

Mapper tests should verify field coverage, null-handling, nested DTO conversion, and preservation of UUID-based identity fields where applicable.

## Current State Note

When DTO families change, these tests should be updated alongside the mapper implementations so serialization contracts remain explicit.
