package com.pawmesh.backend.domain.auth.session;

// Redis 에 JSON 으로 저장되는 회원가입 세션. 업로드 원본 key, 진행 중 jobId, AI 결과를 누적한다.
public record SignupSession(
        String originalImageKey,
        String jobId,
        AiPhotoResult aiResult
) {

    // 업로드 직후 세션 생성
    public static SignupSession of(String originalImageKey) {
        return new SignupSession(originalImageKey, null, null);
    }

    // 진행 중인 AI jobId 기록
    public SignupSession withJobId(String jobId) {
        return new SignupSession(originalImageKey, jobId, aiResult);
    }

    // AI 생성 결과 병합 저장
    public SignupSession withAiResult(AiPhotoResult aiResult) {
        return new SignupSession(originalImageKey, jobId, aiResult);
    }
}
