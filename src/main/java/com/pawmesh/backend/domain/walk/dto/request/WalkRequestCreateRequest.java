package com.pawmesh.backend.domain.walk.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** 산책 요청 보내기 요청 바디 */
public record WalkRequestCreateRequest(

        @NotNull(message = "요청 강아지 ID는 필수입니다.")
        Long requesterDogId,

        @NotNull(message = "대상 강아지 ID는 필수입니다.")
        Long receiverDogId,

        @Size(max = 255, message = "메시지는 255자 이하여야 합니다.")
        String message
) {
}
