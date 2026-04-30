# Custom Repository Implementations

This package contains the concrete implementations for repository fragments declared under `repository.custom`.

## Contents

- `*RepositoryImpl` classes for each custom repository contract

## Responsibility

These classes hold lower-level query logic, typically using JPA infrastructure when the standard Spring Data repository surface is not enough.

## Current State Note

Spring Data relies on naming conventions for fragment wiring. Changes here should stay synchronized with the matching `*RepositoryCustom` interfaces and active repository definitions.
