package com.pawmesh.backend.domain.walk.repository;

import com.pawmesh.backend.domain.walk.entity.WalkRequest;
import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkRequestRepository extends JpaRepository<WalkRequest, Long> {

    // 받은 산책 요청 조회 (특정 강아지가 받은 요청 중 상태별)
    List<WalkRequest> findByReceiverDogIdAndStatus(Long receiverDogId, WalkRequestStatus status);

    // 중복 요청 방지: 같은 상대에게 처리 대기중(PENDING)인 요청이 이미 있는지
    boolean existsByRequesterDogIdAndReceiverDogIdAndStatus(
            Long requesterDogId, Long receiverDogId, WalkRequestStatus status);
}