package com.pawmesh.backend.domain.walk.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.dog.repository.PetRepository;
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
    private final PetRepository petRepository;
    private final WalkRequestConverter walkRequestConverter;

    // 받은 산책 요청 조회 (인증된 사용자의 강아지가 받은 대기중인 요청 목록)
    public List<WalkRequestResponse> getReceived(Long userId) {
        Pet myPet = petRepository.findByUserUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PET_NOT_FOUND));
        List<WalkRequest> requests =
                walkRequestRepository.findByReceiverDog_PetIdAndStatus(myPet.getPetId(), WalkRequestStatus.PENDING);
        return walkRequestConverter.toResponseList(requests);
    }
}
