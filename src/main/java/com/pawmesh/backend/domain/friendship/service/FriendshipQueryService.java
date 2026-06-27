package com.pawmesh.backend.domain.friendship.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.friendship.converter.FriendshipConverter;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipItemResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipListResponse;
import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import com.pawmesh.backend.domain.friendship.repository.FriendshipRepository;
import com.pawmesh.backend.domain.user.entity.Pet;
import com.pawmesh.backend.domain.user.repository.PetRepository;
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
                friendshipRepository.findByDogIdAndStatus(myPet.getPetId(), FriendshipStatus.ACCEPTED);

        // TODO: 친구 강아지 N+1 조회 → findAllById 로 일괄 조회 최적화 검토
        List<FriendshipItemResponse> items = friendships.stream()
                .map(friendship -> {
                    Pet friendDog = petRepository.findById(friendship.getFriendDogId())
                            .orElseThrow(() -> new GeneralException(ErrorStatus.FRIEND_DOG_NOT_FOUND));
                    return friendshipConverter.toItemResponse(friendship, friendDog);
                })
                .toList();

        return friendshipConverter.toListResponse(items);
    }
}
