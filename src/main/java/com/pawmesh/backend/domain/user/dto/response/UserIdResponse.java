package com.pawmesh.backend.domain.user.dto.response;

public record UserIdResponse(
        Long userId
) {
    public static UserIdResponse of(Long userId) {
        return new UserIdResponse(userId);
    }
}
