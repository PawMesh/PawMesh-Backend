package com.pawmesh.backend.domain.auth.dto.response;

public record AiPhotoJobResponse(
        String jobId
) {
    public static AiPhotoJobResponse of(String jobId) {
        return new AiPhotoJobResponse(jobId);
    }
}
