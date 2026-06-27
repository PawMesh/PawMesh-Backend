package com.pawmesh.backend.domain.friendship.repository;

import com.pawmesh.backend.domain.friendship.entity.Friendship;
import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    /** 친구 목록 조회 (특정 강아지의 상태별 친구 관계) */
    List<Friendship> findByDog_PetIdAndStatus(Long dogId, FriendshipStatus status);

    /** 중복 신청 방지: 같은 친구 쌍이 이미 존재하는지 */
    boolean existsByDog_PetIdAndFriendDog_PetId(Long dogId, Long friendDogId);
}
