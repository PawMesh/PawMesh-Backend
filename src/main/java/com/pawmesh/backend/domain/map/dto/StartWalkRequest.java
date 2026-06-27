package com.pawmesh.backend.domain.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 산책 시작 요청 (POST /v1/map-sessions).
 *
 * <p>혼자 산책 모드는 이 API 를 호출하지 않는다.</p>
 */
@Schema(description = "산책 시작 요청")
public record StartWalkRequest(

        @Schema(description = "산책을 시작하는 강아지 ID", example = "1")
        @NotNull(message = "dogId는 필수입니다.")
        Long dogId,

        @Schema(description = "현재 위도", example = "37.4889432")
        @NotNull(message = "currentLat는 필수입니다.")
        Double currentLat,

        @Schema(description = "현재 경도", example = "127.0325871")
        @NotNull(message = "currentLng는 필수입니다.")
        Double currentLng
) {
}
