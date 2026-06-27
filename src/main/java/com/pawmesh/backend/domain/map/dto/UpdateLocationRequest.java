package com.pawmesh.backend.domain.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 위치 업데이트 요청 (PATCH /v1/map-sessions/{sessionId}/location).
 *
 * <p>프론트가 5초마다 호출하며, WALKING/MATCHED 상태일 때만 유효하다.</p>
 */
@Schema(description = "위치 업데이트 요청")
public record UpdateLocationRequest(

        @Schema(description = "현재 위도", example = "37.4891000")
        @NotNull(message = "currentLat는 필수입니다.")
        Double currentLat,

        @Schema(description = "현재 경도", example = "127.0330000")
        @NotNull(message = "currentLng는 필수입니다.")
        Double currentLng
) {
}
