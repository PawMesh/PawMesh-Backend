# PawMesh Backend API Notes

## Common Response

```json
{
  "success": true,
  "data": {},
  "message": null
}
```

## MVP APIs

| Domain | API | Description |
|---|---|---|
| Auth | `POST /auth/anonymous` | Create or retrieve anonymous user and issue access token |
| Dog | `POST /dogs/ai-profile` | Generate dog profile from multipart image |
| Dog | `POST /dogs/me` | Save final dog profile |
| Dog | `GET /dogs/me` | Get my dog profile |
| Owner | `POST /owners/me` | Save owner profile and matching filter |
| Owner | `GET /owners/me` | Get owner profile |
| Map | `POST /walk/start` | Start active walking session |
| Map | `GET /dogs/nearby` | Get active nearby dogs |
| Dog | `GET /dogs/{dogId}` | Get dog detail |
| Request | `POST /walk-requests` | Send walk request |
| Request | `GET /walk-requests/received` | Get received requests |
| Request | `POST /walk-requests/{requestId}/accept` | Accept request |
| Request | `POST /walk-requests/{requestId}/reject` | Reject request |
| Walk | `GET /walk/current` | Get current walk state |
| Walk | `PATCH /walk/location` | Update active walk location |
| Walk | `POST /walk/end` | End active walk |
| Friend | `POST /friends` | Create friendship |
| Friend | `GET /friends` | Get friend list |
| Notification | `GET /notifications` | Get notifications |
| Me | `GET /me` | Get owner and representative dog summary |

## Location Rule

- Store current location on `walk_sessions`, not permanently on users.
- Only active sessions with recent `last_location_updated_at` should appear on the map.
- Hide ended or stale sessions from `GET /dogs/nearby`.
