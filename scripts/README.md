# 📈 Job Application Tracker - Test Coverage Checker

This is a Bash script that checks the Java class-to-test coverage for the **Job Application Tracker** project.

It also optionally runs Gradle tests **per module/folder**!

---

## 🚀 How to Use

```bash
./scripts/check_test_coverage.sh [options]
```

### Options

| Option | Description |
|:------:|:------------|
| `-h`, `--help` | Show this help message and exit |
| `--show-missing`, `--s-m` | Display production classes that are missing corresponding test classes |
| `--with-gradle` | Run Gradle tests for each top-level module |

---

## 📂 Example Usage

**Check overall coverage:**
```bash
./scripts/check_test_coverage.sh
```

**Check coverage and show missing tests:**
```bash
./scripts/check_test_coverage.sh --show-missing
```

**Check coverage + run Gradle tests:**
```bash
./scripts/check_test_coverage.sh --with-gradle
```

**Show help:**
```bash
./scripts/check_test_coverage.sh --help
```

---

## 🛡️ Features

- Folder-by-folder test coverage breakdown
- Colored output:
  - ✅ Green: High coverage (90%+)
  - ⚠️ Yellow: Medium coverage (65%-89%)
  - ❌ Red: Low coverage (<65%)
- Gradle test runner for each folder
- Graceful handling of `CTRL+C` interruptions
- Skips folders without any tests automatically

---

## 📦 Project Structure Expected

```
src/main/java/com/nick/job_application_tracker/...
src/test/java/com/nick/job_application_tracker/...
```

---

