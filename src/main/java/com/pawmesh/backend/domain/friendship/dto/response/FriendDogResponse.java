package com.pawmesh.backend.domain.friendship.dto.response;

/** 친구 목록 항목 내 친구 강아지 정보 */
public record FriendDogResponse(
        Long id,
        String name,
        String characterImageUrl
) {
}
