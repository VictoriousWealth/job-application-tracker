# Frontend Workspace

This directory contains the React/Vite source for the JobTrackr browser UI.

## Core Routes

- `#/overview`: dashboard, recommendations, and upcoming events
- `#/applications`: application queue, sorting, paging, and creation
- `#/applications/:id`: focused application workspace
- `#/library`: reusable resumes, cover letters, sources, and locations
- `#/calendar`: event filtering and exports
- `#/account`: profile and account lifecycle
- `#/admin`: admin-only user and audit views

## Commands

Install dependencies:

```bash
npm install
```

Run the Vite dev server:

```bash
npm run dev
```

Build the production bundle that Spring Boot serves:

```bash
npm run build
```

## Output

The production build is written to `../src/main/resources/static`.

- `index.html`: Spring-served entry shell
- `assets/*.js`: compiled React application bundle
- `assets/*.css`: compiled styles

Treat `src/main/resources/static` as build output, not source code.

## API Flow

The frontend talks to the same-origin backend in production. During local frontend development, the Vite dev server proxies `/api` requests to `http://localhost:8081/`.
