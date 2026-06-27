package com.pawmesh.backend.domain.friendship.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.dog.repository.PetRepository;
import com.pawmesh.backend.domain.friendship.converter.FriendshipConverter;
import com.pawmesh.backend.domain.friendship.dto.request.FriendshipCreateRequest;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipAcceptResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipRejectResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipResponse;
import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.friendship.repository.FriendshipRepository;
import com.pawmesh.backend.domain.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipCommandService {

    private final FriendshipRepository friendshipRepository;
    private final PetRepository petRepository;
    private final WalkSessionRepository walkSessionRepository;
    private final FriendshipConverter friendshipConverter;

    // 친구 신청 (같이 산책한 세션 기반)
    public FriendshipResponse create(FriendshipCreateRequest request) {
        if (friendshipRepository.existsByDog_PetIdAndFriendDog_PetId(request.dogId(), request.friendDogId())) {
            throw new GeneralException(ErrorStatus.FRIENDSHIP_ALREADY_EXISTS);
        }
        // 친구 신청의 근거가 되는 산책 세션이 실제 존재하는지 검증
        // (FRIENDSHIPS 테이블에 walk_session_id 컬럼이 없어 저장은 하지 않고 검증만 수행)
        if (!walkSessionRepository.existsById(request.walkSessionId())) {
            throw new GeneralException(ErrorStatus.WALK_SESSION_NOT_FOUND);
        }
        Pet dog = findPet(request.dogId());
        Pet friendDog = findPet(request.friendDogId());
        Friendship saved = friendshipRepository.save(friendshipConverter.toEntity(dog, friendDog));
        return friendshipConverter.toResponse(saved);
    }

    // 친구 신청 수락
    public FriendshipAcceptResponse accept(Long id) {
        Friendship friendship = findById(id);
        friendship.accept();
        return friendshipConverter.toAcceptResponse(friendship);
    }

    // 친구 신청 거절
    public FriendshipRejectResponse reject(Long id) {
        Friendship friendship = findById(id);
        friendship.reject();
        return friendshipConverter.toRejectResponse(friendship);
    }

    private Friendship findById(Long id) {
        return friendshipRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.FRIENDSHIP_NOT_FOUND));
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PET_NOT_FOUND));
    }
}
