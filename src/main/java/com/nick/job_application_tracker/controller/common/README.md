# Controller Base Types

This package contains shared controller abstractions.

## Contents

- `BaseController`: generic CRUD controller shell built around the shared service contract

## Responsibility

The goal of this package is to centralize common HTTP patterns such as create, get, update, patch, and delete flows.

## Current State Note

The concrete controllers in the repository do not all inherit from this base yet, so treat it as an abstraction point rather than the single active pattern.
