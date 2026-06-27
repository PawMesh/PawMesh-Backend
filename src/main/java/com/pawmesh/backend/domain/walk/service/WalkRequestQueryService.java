package com.pawmesh.backend.domain.walk.service;

import com.pawmesh.backend.domain.walk.converter.WalkRequestConverter;
import com.pawmesh.backend.domain.walk.dto.response.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.entity.WalkRequest;
import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
import com.pawmesh.backend.domain.walk.repository.WalkRequestRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WalkRequestQueryService {

    private final WalkRequestRepository walkRequestRepository;
    private final WalkRequestConverter walkRequestConverter;

    // 받은 산책 요청 조회 (대기중인 요청 목록)
    public List<WalkRequestResponse> getReceived(Long receiverDogId) {
        // TODO: receiverDogId 는 인증된 사용자의 강아지에서 가져오도록 변경
        List<WalkRequest> requests =
                walkRequestRepository.findByReceiverDog_PetIdAndStatus(receiverDogId, WalkRequestStatus.PENDING);
        return walkRequestConverter.toResponseList(requests);
    }
}
