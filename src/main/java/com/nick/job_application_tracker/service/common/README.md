# Common Service Contracts

This package contains generic service abstractions shared across domain services.

## Contents

- `ServiceInterface`: common service contract used as a base abstraction

## Responsibility

The goal of this package is to centralize minimal, reusable service-level conventions without forcing unrelated domains into an overly broad contract.

## Current State Note

Generic service interfaces should stay small. Domain-specific workflows belong in the specialized or concrete service packages instead.
