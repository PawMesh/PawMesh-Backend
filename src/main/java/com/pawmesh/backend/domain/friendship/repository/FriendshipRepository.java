package com.pawmesh.backend.domain.friendship.repository;

import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /** 친구 목록 조회 (특정 강아지의 상태별 친구 관계) */
    List<Friendship> findByDog_PetIdAndStatus(Long dogId, FriendshipStatus status);

    /** 친구 쌍으로 조회 (재신청 시 기존 관계 확인용) */
    Optional<Friendship> findByDog_PetIdAndFriendDog_PetId(Long dogId, Long friendDogId);

    /** 역방향 친구 관계 존재 여부 (수락 시 중복 생성 방지용) */
    boolean existsByDog_PetIdAndFriendDog_PetId(Long dogId, Long friendDogId);
}
