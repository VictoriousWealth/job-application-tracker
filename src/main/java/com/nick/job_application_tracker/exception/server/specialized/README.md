# Specialized Server Exceptions

This package contains targeted 5xx exceptions for infrastructure and reflection-style failures.

## Contents

- `DownstreamApiUnavailableException`: remote dependency outage or unavailability
- `FieldAccessException`: reflective or field-access failure inside server logic

## Responsibility

These types make it easier to distinguish transient platform failures from application-level validation problems.

## Current State Note

If more infrastructure integrations are added later, specialized server exceptions should stay grouped here instead of spreading through unrelated packages.
