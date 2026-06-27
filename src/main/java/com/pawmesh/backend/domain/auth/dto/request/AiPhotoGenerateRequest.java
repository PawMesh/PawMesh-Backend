package com.pawmesh.backend.domain.auth.dto.request;

// AI 생성 시작 요청(선택). petName 은 소개글 프롬프트에 활용된다.
public record AiPhotoGenerateRequest(
        String petName
) {
}
