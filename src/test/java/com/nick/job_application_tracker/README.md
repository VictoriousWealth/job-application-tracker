# Test Suite

This directory contains unit, slice, and integration-oriented tests for the backend packages.

## Package Layout

- `controller`: endpoint-level tests
- `service`: business logic tests
- `repository`: persistence tests
- `mapper`: DTO and entity conversion tests

## Target Testing Standard

The finished repository should aim for tests that verify:

- authentication and authorization boundaries
- ownership scoping by user
- validation and error handling
- repository query correctness
- mapper correctness for all DTO families
- end-to-end behavior of the main application workflows

## Current State Note

The suite includes a broad package spread, but some tests appear to be scaffolds generated to establish structure quickly. As implementation stabilizes, the goal should be to replace thin placeholders with behavior-focused tests.
