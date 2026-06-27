package com.pawmesh.backend.domain.auth.service;

import com.pawmesh.backend.common.storage.ImageStorage;
import com.pawmesh.backend.domain.auth.client.OpenAiClient;
import com.pawmesh.backend.domain.auth.client.PetProfileText;
import com.pawmesh.backend.domain.auth.session.AiPhotoResult;
import com.pawmesh.backend.domain.auth.session.SignupSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// OpenAI 이미지/텍스트 생성을 비동기로 수행하고 결과를 ai-job / signup 세션에 기록한다.
@Slf4j
@Service
@RequiredArgsConstructor
public class AiPhotoGenerationService {

    private static final String AVATAR_DIR = "pet-images/avatar";
    private static final String AVATAR_CONTENT_TYPE = "image/png";

    private final ImageStorage imageStorage;
    private final OpenAiClient openAiClient;
    private final AiJobService aiJobService;
    private final SignupSessionService signupSessionService;

    // 비동기 생성: 성공 시 DONE + 세션 병합, 실패 시 FAILED 만 기록(스레드는 죽이지 않음).
    @Async("aiTaskExecutor")
    public void generateAsync(String signupToken, String jobId, String originalImageKey, String petName) {
        try {
            byte[] original = imageStorage.download(originalImageKey);
            String contentType = contentTypeOf(originalImageKey);

            // 1) 캐릭터 아바타 이미지 생성 → S3 저장
            byte[] avatar = openAiClient.generateAvatar(original, contentType);
            String avatarKey = imageStorage.upload(avatar, AVATAR_CONTENT_TYPE, AVATAR_DIR);
            String avatarImageUrl = imageStorage.getUrl(avatarKey);

            // 2) 견종/성격/소개글 텍스트 생성
            PetProfileText text = openAiClient.generateProfileText(original, contentType, petName);

            AiPhotoResult result = new AiPhotoResult(
                    avatarImageUrl, text.breed(), text.personalityTags(), text.introText());

            aiJobService.markDone(jobId, result);
            mergeIntoSession(signupToken, result);
            log.info("[*] AI photo generation done. jobId={}", jobId);
        } catch (Exception e) {
            // 예외는 잡아서 상태에만 기록 (폴링에서 FAILED 로 노출)
            log.error("[*] AI photo generation failed. jobId={}", jobId, e);
            aiJobService.markFailed(jobId, e.getMessage());
        }
    }

    // DONE 결과를 signup 세션에 병합(complete 에서 사용). 세션 만료 등은 best-effort.
    private void mergeIntoSession(String signupToken, AiPhotoResult result) {
        try {
            SignupSession session = signupSessionService.getOrThrow(signupToken);
            signupSessionService.update(signupToken, session.withAiResult(result));
        } catch (Exception e) {
            log.warn("[*] Failed to merge AI result into signup session: {}", e.getMessage());
        }
    }

    // 저장 key 확장자로 콘텐츠 타입을 추정한다.
    private String contentTypeOf(String key) {
        String lower = key.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/png";
    }
}
