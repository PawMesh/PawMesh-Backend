package com.pawmesh.backend.domain.friendship.dto.response;

import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import java.time.LocalDateTime;

/** 친구 신청 수락 응답 */
public record FriendshipAcceptResponse(
        Long id,
        Long dogId,
        Long friendDogId,
        FriendshipStatus status,
        Integer intimacyLevel,
        Integer walkCount,
        LocalDateTime lastWalkedAt
) {
}
