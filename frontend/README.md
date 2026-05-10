# Frontend Workspace

This directory contains the React/Vite source for the JobTrackr browser UI.

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

## API Flow

The frontend talks to the same-origin backend in production. During local frontend development, the Vite dev server proxies `/api` requests to `http://localhost:8081/`.
