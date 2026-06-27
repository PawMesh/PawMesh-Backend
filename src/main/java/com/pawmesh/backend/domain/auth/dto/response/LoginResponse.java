package com.pawmesh.backend.domain.auth.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long userId
) {
    public static LoginResponse of(String accessToken, String refreshToken, Long userId) {
        return new LoginResponse(accessToken, refreshToken, userId);
    }
}
