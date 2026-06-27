package com.pawmesh.backend.domain.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 산책 완료 요청 (PATCH /v1/map-sessions/{sessionId}/complete).
 *
 * <p>거리·시간·전체 경로를 저장한다. routePath 는 [[lat, lng], ...] 형태의 좌표 배열.</p>
 */
@Schema(description = "산책 완료 요청")
public record CompleteWalkRequest(

        @Schema(description = "총 이동 거리(m)", example = "1240")
        @NotNull(message = "distanceM는 필수입니다.")
        Integer distanceM,

        @Schema(description = "총 산책 시간(초)", example = "1830")
        @NotNull(message = "durationSec는 필수입니다.")
        Integer durationSec,

        @Schema(description = "산책 전체 경로. [[위도, 경도], ...] 형태",
                example = "[[37.4889432, 127.0325871], [37.4891000, 127.0330000]]")
        @NotNull(message = "routePath는 필수입니다.")
        List<List<Double>> routePath
) {
}
