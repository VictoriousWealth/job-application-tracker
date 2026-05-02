# Scripts

This directory contains helper scripts for coverage reporting and test scaffolding support.

## Script Index

| Script | Purpose | Notes |
| --- | --- | --- |
| `check_test_coverage.sh` | Reports class-to-test coverage by package and optionally runs Gradle tests | Useful as a rough structural signal, not as a substitute for behavioral coverage |
| `generate_controllers_tests_templates.sh` | Generates starter controller test files | Produces scaffolds that require manual cleanup |
| `generate_mappers_tests_templates.sh` | Generates starter mapper test files | Produces scaffolds that require manual cleanup |
| `generate_repositories_tests_templates.sh` | Generates starter repository test files | Produces scaffolds that require manual cleanup |
| `generate_services_tests_templates.sh` | Generates starter service test files | Produces scaffolds that require manual cleanup |

## Coverage Checker

Run:

```bash
./scripts/check_test_coverage.sh [options]
```

Options:

- `-h`, `--help`: show help
- `--show-missing`, `--s-m`: list production classes without matching `*Test.java`
- `--with-gradle`: run Gradle tests per top-level package

What it does:

- counts Java classes under `src/main/java/com/nick/job_application_tracker`
- counts test classes under `src/test/java/com/nick/job_application_tracker`
- prints package-by-package coverage percentages
- optionally attempts a Gradle test run for each top-level package

## Template Generator Scripts

The generator scripts create placeholders quickly when a new layer is added. They are useful for bootstrapping package structure, but they do not create production-quality tests.

After running one of the generators, expect to:

- replace placeholder endpoints or assertions
- fix package-specific setup
- add mocks, fixtures, and meaningful assertions
- remove tests that only prove the Spring context starts

## Recommendation

Treat these scripts as development convenience tools. The maintained standard for this repository is behavior-driven testing of the API, services, integrations, and bundled frontend smoke surface. The frontend itself is zero-build and does not rely on this directory.
