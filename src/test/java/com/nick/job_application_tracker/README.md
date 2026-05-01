# Test Suite

This directory contains unit, slice, and integration-oriented tests for the backend packages.

## Package Layout

- `controller`: endpoint-level tests
- `service`: business logic tests
- `integration`: full-stack workflow tests

## Target Testing Standard

The finished repository should aim for tests that verify:

- authentication and authorization boundaries
- ownership scoping by user
- validation and error handling
- repository query correctness
- mapper correctness for all DTO families
- end-to-end behavior of the main application workflows

## Current State Note

The maintained suite is behavior-focused: controller slice tests, service tests for computed workflows, smoke tests for bootstrapping, and end-to-end integration coverage for secured user journeys.
