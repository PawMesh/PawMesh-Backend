package com.pawmesh.backend.domain.friendship.dto.response;

import java.time.LocalDateTime;

/** 친구 목록 항목 */
public record FriendshipItemResponse(
        Long id,
        FriendDogResponse friendDog,
        Integer intimacyLevel,
        Integer walkCount,
        LocalDateTime lastWalkedAt
) {
}
