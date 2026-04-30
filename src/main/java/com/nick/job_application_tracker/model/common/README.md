# Common Model Types

This package contains shared persistence primitives used by multiple domain entities.

## Contents

- `BaseEntity`: common entity fields and lifecycle metadata shared across the model layer

## Responsibility

Shared model types keep identifiers, audit metadata, and other base persistence concerns consistent across all entities.

## Current State Note

The repository has moved to UUID-based identity. Shared entity infrastructure in this package should preserve that consistency.
