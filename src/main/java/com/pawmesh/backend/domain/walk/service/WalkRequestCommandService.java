package com.pawmesh.backend.domain.walk.service;

import com.pawmesh.backend.domain.walk.converter.WalkRequestConverter;
import com.pawmesh.backend.domain.walk.dto.WalkRequestCreateRequest;
import com.pawmesh.backend.domain.walk.dto.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.entity.WalkRequest;
import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
import com.pawmesh.backend.domain.walk.repository.WalkRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 산책 요청 쓰기(Command) 책임 — 생성/상태 변경 */
@Service
@RequiredArgsConstructor
@Transactional
public class WalkRequestCommandService {

    private final WalkRequestRepository walkRequestRepository;

    /** 산책 요청 보내기 */
    public WalkRequestResponse create(WalkRequestCreateRequest request) {
        // 같은 상대에게 이미 대기중(PENDING)인 요청이 있으면 중복 차단
        if (walkRequestRepository.existsByRequesterDogIdAndReceiverDogIdAndStatus(
                request.requesterDogId(), request.receiverDogId(), WalkRequestStatus.PENDING)) {
            // TODO: 커스텀 예외(global/apiPayload)로 교체
            throw new IllegalStateException("이미 대기중인 산책 요청이 있습니다.");
        }
        WalkRequest saved = walkRequestRepository.save(WalkRequestConverter.toEntity(request));
        return WalkRequestConverter.toResponse(saved);
    }

    /** 산책 요청 수락 */
    public WalkRequestResponse accept(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.accept();
        return WalkRequestConverter.toResponse(walkRequest);
    }

    /** 산책 요청 거절 */
    public WalkRequestResponse reject(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.reject();
        return WalkRequestConverter.toResponse(walkRequest);
    }

    /** 산책 요청 취소 */
    public WalkRequestResponse cancel(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.cancel();
        return WalkRequestConverter.toResponse(walkRequest);
    }

    private WalkRequest findById(Long id) {
        return walkRequestRepository.findById(id)
                // TODO: 커스텀 예외(global/apiPayload)로 교체
                .orElseThrow(() -> new IllegalArgumentException("산책 요청을 찾을 수 없습니다. id=" + id));
    }
}
