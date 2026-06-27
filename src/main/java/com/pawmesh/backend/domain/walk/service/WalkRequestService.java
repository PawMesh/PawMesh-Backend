package com.pawmesh.backend.domain.walk.service;

import com.pawmesh.backend.domain.walk.converter.WalkRequestConverter;
import com.pawmesh.backend.domain.walk.dto.WalkRequestCreateRequest;
import com.pawmesh.backend.domain.walk.dto.WalkRequestResponse;
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
public class WalkRequestService {

    private final WalkRequestRepository walkRequestRepository;

    /** 산책 요청 보내기 */
    @Transactional
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

    /** 받은 산책 요청 조회 (대기중인 요청 목록) */
    public List<WalkRequestResponse> getReceived(Long receiverDogId) {
        // TODO: receiverDogId 는 인증된 사용자의 강아지에서 가져오도록 변경
        List<WalkRequest> requests =
                walkRequestRepository.findByReceiverDogIdAndStatus(receiverDogId, WalkRequestStatus.PENDING);
        return WalkRequestConverter.toResponseList(requests);
    }

    /** 산책 요청 수락 */
    @Transactional
    public WalkRequestResponse accept(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.accept();
        return WalkRequestConverter.toResponse(walkRequest);
    }

    /** 산책 요청 거절 */
    @Transactional
    public WalkRequestResponse reject(Long id) {
        WalkRequest walkRequest = findById(id);
        walkRequest.reject();
        return WalkRequestConverter.toResponse(walkRequest);
    }

    /** 산책 요청 취소 */
    @Transactional
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
