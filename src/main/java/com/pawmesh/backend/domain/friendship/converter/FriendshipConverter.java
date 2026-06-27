package com.pawmesh.backend.domain.friendship.converter;

import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.friendship.dto.response.FriendDogResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipAcceptResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipItemResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipListResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipRejectResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipResponse;
import com.pawmesh.backend.domain.friendship.entity.Friendship;
import java.util.List;
import org.springframework.stereotype.Component;

// 친구 신청 요청/엔티티/응답 간 변환을 담당한다.
@Component
public class FriendshipConverter {

    // 요청 강아지/친구 강아지 → 엔티티
    public Friendship toEntity(Pet dog, Pet friendDog) {
        return Friendship.create(dog, friendDog);
    }

    // 엔티티 → 친구 신청 응답
    public FriendshipResponse toResponse(Friendship friendship) {
        return new FriendshipResponse(
                friendship.getId(),
                friendship.getDog().getPetId(),
                friendship.getFriendDog().getPetId(),
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
                friendship.getDog().getPetId(),
                friendship.getFriendDog().getPetId(),
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

    // 엔티티 → 친구 목록 항목 (친구 강아지는 연관관계로 직접 접근)
    public FriendshipItemResponse toItemResponse(Friendship friendship) {
        return new FriendshipItemResponse(
                friendship.getId(),
                toFriendDogResponse(friendship.getFriendDog()),
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
