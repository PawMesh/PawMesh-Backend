package com.pawmesh.backend.domain.walk.dto;

import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
import java.time.LocalDateTime;

/** 산책 요청 응답 (보내기/조회/수락/거절/취소 공통) */
public record WalkRequestResponse(
        Long id,
        Long requesterDogId,
        Long receiverDogId,
        String message,
        WalkRequestStatus status,
        Long walkSessionId,
        LocalDateTime createdAt,
        LocalDateTime respondedAt
) {
}
