package com.pawmesh.backend.domain.friendship.dto.request;

import jakarta.validation.constraints.NotNull;

/** 친구 신청 요청 바디 */
public record FriendshipCreateRequest(

        @NotNull(message = "신청 강아지 ID는 필수입니다.")
        Long dogId,

        @NotNull(message = "친구 강아지 ID는 필수입니다.")
        Long friendDogId,

        @NotNull(message = "산책 세션 ID는 필수입니다.")
        Long walkSessionId
) {
}
