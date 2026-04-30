# Utility Classes

This package contains small helper utilities that do not belong naturally inside controllers, services, repositories, or configuration classes.

## Current Utility

- `SecurityUtils`: helper methods for resolving the current authenticated user context

## Design Rule

Utility classes should remain narrow. If a helper starts owning business rules or persistence decisions, it should move into a proper service instead.
