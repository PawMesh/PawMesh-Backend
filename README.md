# PawMesh Backend

PawMesh Spring Boot backend repository.

PawMesh is a pet walking social app that connects nearby dogs and guardians through map-based walking sessions.

## Role

- Anonymous authentication for hackathon MVP
- Dog and owner profile APIs
- AI dog profile generation endpoint
- Nearby dog lookup by active walk location
- Walk request and walk session APIs
- Friend and intimacy data
- Notification APIs

## Related Repositories

- Android: https://github.com/PawMesh/PawMesh-Android
- Docs hub: https://github.com/PawMesh/Paw_Mesh

## Suggested Structure

```txt
PawMesh-Backend/
├─ src/main/java/com/pawmesh/backend/
├─ src/main/resources/
├─ docs/
└─ .github/
```

## MVP Domains

- `User`: anonymous login account
- `Owner`: guardian profile and matching filter
- `Dog`: dog profile and AI-generated character image
- `WalkSession`: active walk and location state
- `WalkRequest`: request, accept, reject
- `Friendship`: friend relation and intimacy level
- `Notification`: request, accept, friend notifications

## Branch Convention

```txt
feat/{feature-name}
fix/{bug-name}
chore/{task-name}
docs/{doc-name}
```

## Commit Convention

```txt
feat: add anonymous auth api
fix: filter inactive walk sessions
chore: configure spring boot project
docs: update API contract
```
