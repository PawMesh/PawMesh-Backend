package com.pawmesh.backend.domain.map.dto.response;

import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
import java.util.List;

// 강아지 카드 응답 (지도 마커 탭 시 상세).
public record DogCardResponse(
        Long dogId,
        String name,
        String breed,
        String size,
        String characterImageUrl,
        List<String> tags,
        List<String> ownerWalkStyles,
        WalkSessionStatus status,
        String cautionNote,
        Integer intimacyLevel
) {
}
