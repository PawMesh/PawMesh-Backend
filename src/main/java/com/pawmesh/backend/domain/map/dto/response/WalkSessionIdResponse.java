package com.pawmesh.backend.domain.map.dto.response;

// 산책 시작/위치 업데이트/종료 공통 응답 (세션 ID).
public record WalkSessionIdResponse(
        Long walkSessionId
) {
    public static WalkSessionIdResponse of(Long walkSessionId) {
        return new WalkSessionIdResponse(walkSessionId);
    }
}
