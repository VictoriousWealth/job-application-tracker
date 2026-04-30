# Exception Hierarchy

This package defines the domain-specific exception model for the backend.

## Main Categories

- `base_case`: shared exception base classes
- `client_exception`: errors caused by invalid input, missing resources, forbidden actions, or similar client-side conditions
- `server_exception`: internal failures and unavailable downstream or platform conditions

## Purpose

The exception hierarchy exists so that:

- services can express intent clearly
- controllers do not need ad hoc error handling
- the global exception handler can map failures to stable HTTP responses
- logs can distinguish between expected client mistakes and real server faults

## Target Usage

- throw client exceptions for validation, conflict, authorization, and not-found paths
- throw server exceptions for platform, infrastructure, or unexpected service failures
- avoid using raw `RuntimeException` where a domain-specific exception communicates intent better
