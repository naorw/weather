# 0002 — Frontend toolchain

## Status

Accepted

## Context

Phase 0 needs a TypeScript web app, local dev server, production build, linting, and automated tests. The product must not inherit a component library visual language.

## Decision

Use:

- Vite for development and production builds
- Vanilla TypeScript (no React, Vue, or Svelte)
- Vitest + happy-dom for unit tests
- ESLint with typescript-eslint
- No general-purpose client state library

## Consequences

UI is composed from small TypeScript modules and CSS. Later phases should not introduce a UI kit that imposes Material or similar chrome. A renderer library may be reconsidered only if Phase 0 structure becomes a proven bottleneck.
