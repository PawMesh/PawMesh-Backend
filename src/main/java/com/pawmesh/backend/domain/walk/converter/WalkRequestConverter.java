package com.pawmesh.backend.domain.walk.converter;

import com.pawmesh.backend.domain.walk.dto.WalkRequestCreateRequest;
import com.pawmesh.backend.domain.walk.dto.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.entity.WalkRequest;
import java.util.List;

public class WalkRequestConverter {

    private WalkRequestConverter() {
    }

    /** 요청 DTO → 엔티티 (status 는 엔티티 빌더에서 PENDING 으로 초기화) */
    public static WalkRequest toEntity(WalkRequestCreateRequest request) {
        return WalkRequest.builder()
                .requesterDogId(request.requesterDogId())
                .receiverDogId(request.receiverDogId())
                .message(request.message())
                .build();
    }

    /** 엔티티 → 응답 DTO */
    public static WalkRequestResponse toResponse(WalkRequest entity) {
        return new WalkRequestResponse(
                entity.getId(),
                entity.getRequesterDogId(),
                entity.getReceiverDogId(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getWalkSessionId(),
                null, // TODO: BaseEntity 상속 후 entity.getCreatedAt() 으로 교체
                entity.getRespondedAt()
        );
    }

    /** 엔티티 목록 → 응답 DTO 목록 (받은 요청 조회용) */
    public static List<WalkRequestResponse> toResponseList(List<WalkRequest> entities) {
        return entities.stream()
                .map(WalkRequestConverter::toResponse)
                .toList();
    }
}
