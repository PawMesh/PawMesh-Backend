package com.pawmesh.backend.domain.friendship.dto.response;

import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import java.time.LocalDateTime;

/** 친구 신청 응답 */
public record FriendshipResponse(
        Long id,
        Long dogId,
        Long friendDogId,
        FriendshipStatus status,
        Integer intimacyLevel,
        Integer walkCount,
        LocalDateTime createdAt
) {
}
