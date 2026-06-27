package com.pawmesh.backend.domain.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 산책 완료 응답 (PATCH /v1/map-sessions/{sessionId}/complete).
 */
@Schema(description = "산책 완료 응답")
public record CompleteWalkResponse(

        @Schema(description = "산책 세션 ID", example = "123")
        Long walkSessionId,

        @Schema(description = "총 이동 거리(m)", example = "1240")
        Integer distanceM,

        @Schema(description = "총 산책 시간(초)", example = "1830")
        Integer durationSec
) {
}
