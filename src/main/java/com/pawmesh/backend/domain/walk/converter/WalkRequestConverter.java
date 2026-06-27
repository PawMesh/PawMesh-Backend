package com.pawmesh.backend.domain.walk.converter;

import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.walk.dto.response.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.entity.WalkRequest;
import java.util.List;
import org.springframework.stereotype.Component;

// 산책 요청 요청/엔티티/응답 간 변환을 담당한다.
@Component
public class WalkRequestConverter {

    // 요청 강아지/대상 강아지 → 엔티티 (status 는 엔티티에서 PENDING 으로 초기화)
    public WalkRequest toEntity(Pet requesterDog, Pet receiverDog, String message) {
        return WalkRequest.create(requesterDog, receiverDog, message);
    }

    // 엔티티 → 응답 DTO
    public WalkRequestResponse toResponse(WalkRequest entity) {
        return new WalkRequestResponse(
                entity.getId(),
                entity.getRequesterDog().getPetId(),
                entity.getReceiverDog().getPetId(),
                entity.getMessage(),
                entity.getStatus(),
                entity.getWalkSession() != null ? entity.getWalkSession().getId() : null,
                entity.getCreatedAt(),
                entity.getRespondedAt()
        );
    }

    // 엔티티 목록 → 응답 DTO 목록 (받은 요청 조회용)
    public List<WalkRequestResponse> toResponseList(List<WalkRequest> entities) {
        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
