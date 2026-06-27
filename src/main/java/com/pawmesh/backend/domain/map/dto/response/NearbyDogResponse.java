package com.pawmesh.backend.domain.map.dto.response;

import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
import java.util.List;

// 주변 강아지(마커) 응답 항목.
public record NearbyDogResponse(
        Long walkSessionId,
        Long dogId,
        String characterImageUrl,
        Double lat,
        Double lng,
        WalkSessionStatus status,
        List<String> tags
) {
}
