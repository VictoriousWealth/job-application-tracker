# Service Interfaces

This package contains reusable service contracts that are shared across the service layer.

## Contents

- `AuditLogService`: audit logging contract used by mutating services

## Expectations For These Services

Contracts here should:

- stay small and cross-cutting
- define behavior that multiple concrete services depend on
- avoid duplicating domain-specific logic that belongs in implementations

## Current State Note

Most domain workflows now live directly in the concrete services under `service.implementation`. This package is intentionally narrow.
