package com.pawmesh.backend.domain.friendship.dto.response;

import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;

/** 친구 신청 거절 응답 */
public record FriendshipRejectResponse(
        Long id,
        FriendshipStatus status
) {
}
