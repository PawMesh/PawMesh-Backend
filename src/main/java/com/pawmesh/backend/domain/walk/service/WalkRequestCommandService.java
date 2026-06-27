package com.pawmesh.backend.domain.walk.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.dog.repository.PetRepository;
import com.pawmesh.backend.domain.walk.converter.WalkRequestConverter;
import com.pawmesh.backend.domain.walk.dto.request.WalkRequestCreateRequest;
import com.pawmesh.backend.domain.walk.dto.response.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.entity.WalkRequest;
import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
import com.pawmesh.backend.domain.walk.repository.WalkRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WalkRequestCommandService {

    private final WalkRequestRepository walkRequestRepository;
    private final PetRepository petRepository;
    private final WalkRequestConverter walkRequestConverter;

    // 산책 요청 보내기
    public WalkRequestResponse create(WalkRequestCreateRequest request) {
        if (walkRequestRepository.existsByRequesterDog_PetIdAndReceiverDog_PetIdAndStatus(
                request.requesterDogId(), request.receiverDogId(), WalkRequestStatus.PENDING)) {
            throw new GeneralException(ErrorStatus.WALK_REQUEST_ALREADY_EXISTS);
        }
        Pet requesterDog = findPet(request.requesterDogId());
        Pet receiverDog = findPet(request.receiverDogId());
        WalkRequest saved = walkRequestRepository.save(
                walkRequestConverter.toEntity(requesterDog, receiverDog, request.message()));
        return walkRequestConverter.toResponse(saved);
    }

    // 산책 요청 수락
    public WalkRequestResponse accept(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.accept();
        return walkRequestConverter.toResponse(walkRequest);
    }

    // 산책 요청 거절
    public WalkRequestResponse reject(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.reject();
        return walkRequestConverter.toResponse(walkRequest);
    }

    // 산책 요청 취소
    public WalkRequestResponse cancel(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.cancel();
        return walkRequestConverter.toResponse(walkRequest);
    }

    private WalkRequest findById(Long id) {
        return walkRequestRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WALK_REQUEST_NOT_FOUND));
    }

    private Pet findPet(Long petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PET_NOT_FOUND));
    }
}
