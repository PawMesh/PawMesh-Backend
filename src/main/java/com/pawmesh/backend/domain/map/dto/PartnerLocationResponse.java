package com.pawmesh.backend.domain.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 산책 친구(파트너) 위치 조회 응답 (GET /v1/map-sessions/{sessionId}/partner-location).
 *
 * <p>세션이 MATCHED 상태가 아니면 404 로 응답한다(실제 로직은 DB 연동 후 구현).</p>
 */
@Schema(description = "산책 친구 위치 응답")
public record PartnerLocationResponse(

        @Schema(description = "파트너 강아지 ID", example = "9")
        Long partnerDogId,

        @Schema(description = "파트너 AI 캐릭터 이미지 URL", example = "https://cdn.pawmesh.app/dogs/9/character.png")
        String characterImageUrl,

        @Schema(description = "위도", example = "37.4893500")
        Double lat,

        @Schema(description = "경도", example = "127.0331200")
        Double lng
) {
}
