package com.pawmesh.backend.domain.map.dto.request;

// 산책 시작 요청. 혼자 산책 모드는 호출하지 않는다.
public record StartWalkRequest(
        Long dogId,
        Double currentLat,
        Double currentLng
) {
}
