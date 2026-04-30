# Create DTOs

This package contains request models used when creating new resources.

## Contents

- create DTOs for applications, documents, communications, reminders, timeline entries, metadata, and skills

## Responsibility

These classes define the accepted write shape for `POST` operations and should contain validation rules for required fields and basic format checks.

## Current State Note

The package is the main entry point for creation payloads and should stay persistence-agnostic.
