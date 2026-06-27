package com.pawmesh.backend.domain.auth.session;

import com.pawmesh.backend.domain.auth.enums.AiJobStatus;

// Redis 에 JSON 으로 저장되는 AI 생성 작업 상태. (ai-job:{jobId})
public record AiJob(
        AiJobStatus status,
        AiPhotoResult result,
        String error
) {

    public static AiJob processing() {
        return new AiJob(AiJobStatus.PROCESSING, null, null);
    }

    public static AiJob done(AiPhotoResult result) {
        return new AiJob(AiJobStatus.DONE, result, null);
    }

    public static AiJob failed(String error) {
        return new AiJob(AiJobStatus.FAILED, null, error);
    }
}
