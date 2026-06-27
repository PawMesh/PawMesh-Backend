package com.pawmesh.backend.domain.auth.dto.response;

public record CheckNicknameResponse(
        boolean available
) {
    public static CheckNicknameResponse of(boolean available) {
        return new CheckNicknameResponse(available);
    }
}
