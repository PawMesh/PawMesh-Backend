package com.pawmesh.backend.domain.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.properties.SignupProperties;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.auth.session.SignupSession;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

// X-Signup-Token 기반 회원가입 세션을 Redis 로 관리한다. Task 2/3/4 에서 재사용한다.
@Service
@RequiredArgsConstructor
public class SignupSessionService {

    private static final String KEY_PREFIX = "signup:";

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final SignupProperties signupProperties;

    // 새 세션을 생성하고 불투명 토큰(UUID)을 반환한다.
    public String create(SignupSession session) {
        String token = UUID.randomUUID().toString();
        save(token, session);
        return token;
    }

    // 토큰으로 세션을 조회한다. 토큰 없음/세션 만료 시 예외.
    public SignupSession getOrThrow(String token) {
        if (!StringUtils.hasText(token)) {
            throw new GeneralException(ErrorStatus.SIGNUP_TOKEN_REQUIRED);
        }
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        if (json == null) {
            throw new GeneralException(ErrorStatus.SIGNUP_SESSION_EXPIRED);
        }
        return deserialize(json);
    }

    // 세션을 갱신한다(TTL 재설정).
    public void update(String token, SignupSession session) {
        save(token, session);
    }

    // 세션을 삭제한다.
    public void delete(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }

    // 직렬화하여 TTL 과 함께 저장
    private void save(String token, SignupSession session) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + token,
                serialize(session),
                Duration.ofMinutes(signupProperties.sessionTtlMinutes())
        );
    }

    private String serialize(SignupSession session) {
        try {
            return objectMapper.writeValueAsString(session);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private SignupSession deserialize(String json) {
        try {
            return objectMapper.readValue(json, SignupSession.class);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
