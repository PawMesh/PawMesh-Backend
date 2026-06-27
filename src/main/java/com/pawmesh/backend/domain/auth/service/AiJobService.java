package com.pawmesh.backend.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.properties.SignupProperties;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.auth.session.AiJob;
import com.pawmesh.backend.domain.auth.session.AiPhotoResult;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

// AI 생성 작업 상태를 Redis(ai-job:{jobId}) 로 관리한다.
@Service
@RequiredArgsConstructor
public class AiJobService {

    private static final String KEY_PREFIX = "ai-job:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SignupProperties signupProperties;

    // 작업 상태를 저장한다(TTL 재설정).
    public void save(String jobId, AiJob job) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + jobId,
                serialize(job),
                Duration.ofMinutes(signupProperties.sessionTtlMinutes())
        );
    }

    // 작업 상태를 조회한다. 없으면 예외.
    public AiJob getOrThrow(String jobId) {
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + jobId);
        if (json == null) {
            throw new GeneralException(ErrorStatus.AI_JOB_NOT_FOUND);
        }
        return deserialize(json);
    }

    // 성공 처리
    public void markDone(String jobId, AiPhotoResult result) {
        save(jobId, AiJob.done(result));
    }

    // 실패 처리 (예외는 잡아서 상태에만 기록)
    public void markFailed(String jobId, String error) {
        save(jobId, AiJob.failed(error));
    }

    // 작업 상태를 삭제한다. (회원가입 완료 시 정리)
    public void delete(String jobId) {
        redisTemplate.delete(KEY_PREFIX + jobId);
    }

    private String serialize(AiJob job) {
        try {
            return objectMapper.writeValueAsString(job);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private AiJob deserialize(String json) {
        try {
            return objectMapper.readValue(json, AiJob.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
