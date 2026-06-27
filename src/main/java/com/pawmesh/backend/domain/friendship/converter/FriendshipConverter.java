package com.pawmesh.backend.domain.friendship.converter;

import com.pawmesh.backend.domain.friendship.dto.request.FriendshipCreateRequest;
import com.pawmesh.backend.domain.friendship.dto.response.FriendDogResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipAcceptResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipItemResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipListResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipRejectResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipResponse;
import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.user.entity.Pet;
import java.util.List;
import org.springframework.stereotype.Component;

// 친구 신청 요청/엔티티/응답 간 변환을 담당한다.
@Component
public class FriendshipConverter {

    // 친구 신청 요청 → 엔티티
    public Friendship toEntity(FriendshipCreateRequest request) {
        return Friendship.create(request.dogId(), request.friendDogId());
    }

    // 엔티티 → 친구 신청 응답
    public FriendshipResponse toResponse(Friendship friendship) {
        return new FriendshipResponse(
                friendship.getId(),
                friendship.getDogId(),
                friendship.getFriendDogId(),
                friendship.getStatus(),
                friendship.getIntimacyLevel(),
                friendship.getWalkCount(),
                friendship.getCreatedAt()
        );
    }

    // 엔티티 → 수락 응답
    public FriendshipAcceptResponse toAcceptResponse(Friendship friendship) {
        return new FriendshipAcceptResponse(
                friendship.getId(),
                friendship.getDogId(),
                friendship.getFriendDogId(),
                friendship.getStatus(),
                friendship.getIntimacyLevel(),
                friendship.getWalkCount(),
                friendship.getLastWalkedAt()
        );
    }

    // 엔티티 → 거절 응답
    public FriendshipRejectResponse toRejectResponse(Friendship friendship) {
        return new FriendshipRejectResponse(
                friendship.getId(),
                friendship.getStatus()
        );
    }

    // 친구 강아지(Pet) → 친구 강아지 정보 응답
    public FriendDogResponse toFriendDogResponse(Pet friendDog) {
        return new FriendDogResponse(
                friendDog.getPetId(),
                friendDog.getPetName(),
                friendDog.getAvatarImageUrl()
        );
    }

    // 엔티티 + 친구 강아지 → 친구 목록 항목
    public FriendshipItemResponse toItemResponse(Friendship friendship, Pet friendDog) {
        return new FriendshipItemResponse(
                friendship.getId(),
                toFriendDogResponse(friendDog),
                friendship.getIntimacyLevel(),
                friendship.getWalkCount(),
                friendship.getLastWalkedAt()
        );
    }

    // 항목 목록 → 친구 목록 조회 응답
    public FriendshipListResponse toListResponse(List<FriendshipItemResponse> items) {
        return new FriendshipListResponse(items);
    }
}
