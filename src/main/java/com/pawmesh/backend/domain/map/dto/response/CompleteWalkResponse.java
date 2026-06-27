package com.pawmesh.backend.domain.map.dto.response;

// 산책 완료 응답.
public record CompleteWalkResponse(
        Long walkSessionId,
        Integer distanceM,
        Integer durationSec
) {
    public static CompleteWalkResponse of(Long walkSessionId, Integer distanceM, Integer durationSec) {
        return new CompleteWalkResponse(walkSessionId, distanceM, durationSec);
    }
}
