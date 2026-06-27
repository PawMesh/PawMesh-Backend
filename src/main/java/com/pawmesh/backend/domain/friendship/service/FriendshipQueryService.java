package com.pawmesh.backend.domain.friendship.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.dog.repository.PetRepository;
import com.pawmesh.backend.domain.friendship.converter.FriendshipConverter;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipItemResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipListResponse;
import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import com.pawmesh.backend.domain.friendship.repository.FriendshipRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipQueryService {

    private final FriendshipRepository friendshipRepository;
    private final PetRepository petRepository;
    private final FriendshipConverter friendshipConverter;

    // 친구 목록 조회 (인증된 사용자의 강아지가 수락(ACCEPTED)한 친구 목록)
    public FriendshipListResponse getFriendships(Long userId) {
        Pet myPet = petRepository.findByUserUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<Friendship> friendships =
                friendshipRepository.findByDog_PetIdAndStatus(myPet.getPetId(), FriendshipStatus.ACCEPTED);

        // 친구 강아지는 @ManyToOne 연관관계로 직접 접근 (별도 조회 불필요)
        List<FriendshipItemResponse> items = friendships.stream()
                .map(friendshipConverter::toItemResponse)
                .toList();

        return friendshipConverter.toListResponse(items);
    }
}
