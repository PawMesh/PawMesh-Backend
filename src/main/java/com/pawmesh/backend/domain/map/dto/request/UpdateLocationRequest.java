package com.pawmesh.backend.domain.map.dto.request;

// 위치 업데이트 요청. 프론트가 5초마다 호출한다.
public record UpdateLocationRequest(
        Double currentLat,
        Double currentLng
) {
}
