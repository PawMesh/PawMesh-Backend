package com.pawmesh.backend.domain.auth.session;

import java.util.List;

// AI 가 생성한 펫 프로필 결과. signup 세션 / AI job 결과 / 폴링 응답에 공통으로 쓰인다.
public record AiPhotoResult(
        String avatarImageUrl,
        String breed,
        List<String> personalityTags,
        String introText
) {
}
