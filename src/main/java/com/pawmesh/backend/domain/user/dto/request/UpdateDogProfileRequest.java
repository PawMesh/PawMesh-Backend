package com.pawmesh.backend.domain.user.dto.request;

import java.util.List;

// 강아지 프로필 부분 수정 요청 (PATCH). 변경할 필드만 채워 보낸다.
public record UpdateDogProfileRequest(
        String petName,
        String breed,
        List<String> personalityTags,
        String caution,
        String introText
) {
}
