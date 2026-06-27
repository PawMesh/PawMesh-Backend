package com.pawmesh.backend.domain.auth.dto.response;

public record UploadPetImageResponse(
        String signupToken,
        String originalImageUrl
) {
    public static UploadPetImageResponse of(String signupToken, String originalImageUrl) {
        return new UploadPetImageResponse(signupToken, originalImageUrl);
    }
}
