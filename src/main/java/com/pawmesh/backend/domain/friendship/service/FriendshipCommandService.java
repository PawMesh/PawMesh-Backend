package com.pawmesh.backend.domain.friendship.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.friendship.converter.FriendshipConverter;
import com.pawmesh.backend.domain.friendship.dto.request.FriendshipCreateRequest;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipAcceptResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipRejectResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipResponse;
import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.friendship.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipCommandService {

    private final FriendshipRepository friendshipRepository;
    private final FriendshipConverter friendshipConverter;

    // 친구 신청
    public FriendshipResponse create(FriendshipCreateRequest request) {
        if (friendshipRepository.existsByDogIdAndFriendDogId(request.dogId(), request.friendDogId())) {
            throw new GeneralException(ErrorStatus.FRIENDSHIP_ALREADY_EXISTS);
        }
        // TODO: walkSessionId 검증/활용 (FRIENDSHIPS 테이블에 컬럼이 없어 현재는 미저장)
        Friendship saved = friendshipRepository.save(friendshipConverter.toEntity(request));
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
}
