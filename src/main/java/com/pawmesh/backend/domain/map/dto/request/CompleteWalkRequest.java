package com.pawmesh.backend.domain.map.dto.request;

import java.util.List;

// 산책 완료 요청. routePath 는 [[lat, lng], ...] 형태의 전체 경로.
public record CompleteWalkRequest(
        Integer distanceM,
        Integer durationSec,
        List<List<Double>> routePath
) {
}
