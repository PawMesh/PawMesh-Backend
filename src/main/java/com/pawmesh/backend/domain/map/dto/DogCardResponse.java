package com.pawmesh.backend.domain.map.dto;

import com.pawmesh.backend.domain.map.enums.WalkSessionStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 강아지 카드 조회 응답 (GET /v1/map-sessions/{sessionId}/dog-card).
 *
 * <p>지도 마커를 탭했을 때 보여줄 상세 카드 정보.</p>
 */
@Schema(description = "강아지 카드 응답")
public record DogCardResponse(

        @Schema(description = "강아지 ID", example = "7")
        Long dogId,

        @Schema(description = "이름", example = "콩이")
        String name,

        @Schema(description = "견종", example = "포메라니안")
        String breed,

        @Schema(description = "크기", example = "소형견")
        String size,

        @Schema(description = "AI 캐릭터 이미지 URL", example = "https://cdn.pawmesh.app/dogs/7/character.png")
        String characterImageUrl,

        @Schema(description = "강아지 태그 목록", example = "[\"소형견\", \"온순\"]")
        List<String> tags,

        @Schema(description = "보호자 산책 스타일 목록", example = "[\"천천히 걷기\", \"공원 선호\"]")
        List<String> ownerWalkStyles,

        WalkSessionStatus status,

        @Schema(description = "주의사항", example = "낯선 사람을 경계해요")
        String cautionNote,

        @Schema(description = "나와의 친밀도 레벨(1부터)", example = "1")
        Integer intimacyLevel
) {
}
