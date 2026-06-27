package com.pawmesh.backend.domain.friendship.dto.response;

import java.util.List;

/** 친구 목록 조회 응답 */
public record FriendshipListResponse(
        List<FriendshipItemResponse> items
) {
}
