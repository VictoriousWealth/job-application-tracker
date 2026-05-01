# Specialized Client Exceptions

This package contains targeted 4xx exceptions for patching and field-level validation scenarios.

## Contents

- `InvalidFieldException`: invalid or unsupported field usage
- `PatchException`: malformed or unsupported patch operations
- `InvalidJobApplicationException`: job-application-specific validation failure

## Responsibility

These types provide more precise failure semantics when the generic client exception classes are too broad for controller or service logic.

## Current State Note

Prefer reusing the generic client exception hierarchy unless a more specific type materially improves diagnostics or handler behavior.
