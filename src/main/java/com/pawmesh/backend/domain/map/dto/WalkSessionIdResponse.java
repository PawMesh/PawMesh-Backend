package com.pawmesh.backend.domain.map.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 산책 세션 ID 만 돌려주는 공통 응답.
 *
 * <p>산책 시작 / 위치 업데이트 / 지도 삭제(종료) 응답에 공통으로 쓰인다.</p>
 */
@Schema(description = "산책 세션 ID 응답")
public record WalkSessionIdResponse(

        @Schema(description = "산책 세션 ID", example = "123")
        Long walkSessionId
) {
    public static WalkSessionIdResponse of(Long walkSessionId) {
        return new WalkSessionIdResponse(walkSessionId);
    }
}
