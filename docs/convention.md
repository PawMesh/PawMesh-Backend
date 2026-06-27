# Backend Convention

## Package

Use a package name under:

```txt
com.pawmesh.backend
```

## API

- Use REST endpoints from `docs/api.md`.
- Keep request and response DTOs explicit.
- Use `success`, `data`, `message` as the common response shape for MVP.

## Location

- Do not store live location directly on `users` or `owners`.
- Store active walking location on `walk_sessions`.
- Filter stale sessions when returning nearby dogs.
