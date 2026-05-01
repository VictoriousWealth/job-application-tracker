# Integration Tests

This package contains full-stack tests that exercise multiple layers together through the real Spring Boot application context.

## Responsibility

Integration tests should cover cross-layer workflows such as authenticated job-application lifecycle flows, ownership scoping, persistence behavior, and application startup wiring.

## Current State Note

- `JobSearchWorkflowIntegrationTest`: verifies the secured end-to-end user workflow, including signup/login, application creation, related records, insights, exports, calendar output, matching, and ownership isolation.
