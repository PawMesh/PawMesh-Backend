package com.pawmesh.backend.domain.map.dto;

import com.pawmesh.backend.domain.map.enums.WalkSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 주변 강아지 조회 응답 항목 (GET /v1/map-sessions/nearby).
 *
 * <p>지도 위 마커 하나에 대응한다.</p>
 */
@Schema(description = "주변 강아지(마커) 응답 항목")
public record NearbyDogResponse(

        @Schema(description = "산책 세션 ID", example = "456")
        Long walkSessionId,

        @Schema(description = "강아지 ID", example = "7")
        Long dogId,

        @Schema(description = "AI 캐릭터 이미지 URL", example = "https://cdn.pawmesh.app/dogs/7/character.png")
        String characterImageUrl,

        @Schema(description = "위도", example = "37.4892100")
        Double lat,

        @Schema(description = "경도", example = "127.0328400")
        Double lng,

        @Schema(description = "세션 상태", example = "WALKING")
        WalkSessionStatus status,

        @Schema(description = "강아지 태그 목록", example = "[\"소형견\", \"온순\"]")
        List<String> tags
) {
}
