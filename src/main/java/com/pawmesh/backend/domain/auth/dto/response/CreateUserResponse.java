package com.pawmesh.backend.domain.auth.dto.response;

public record CreateUserResponse(
        Long userId
) {
    public static CreateUserResponse of(Long userId) {
        return new CreateUserResponse(userId);
    }
}
