package com.pawmesh.backend.domain.auth.client;

import java.util.List;

// OpenAI chat/completions 가 생성한 텍스트 결과(견종/성격태그/소개글).
public record PetProfileText(
        String breed,
        List<String> personalityTags,
        String introText
) {
}
