# Detail DTOs

This package contains richer single-resource views returned from detail endpoints.

## Contents

- detail DTOs for applications, timeline events, documents, communications, reminders, metadata, and audit records

## Responsibility

These classes should expose enough information for a client to render a focused record view without leaking JPA entities directly.

## Current State Note

When a controller returns a single resource by ID, this package is usually the default output family.
