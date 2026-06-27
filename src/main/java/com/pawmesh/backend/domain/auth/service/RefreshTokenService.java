package com.pawmesh.backend.domain.auth.service;

import com.pawmesh.backend.common.properties.JwtProperties;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

// refresh 토큰을 Redis(refresh:{userId}) 로 저장/검증한다. (회전 및 폐기 가능)
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

    // 저장(TTL = refresh 유효기간)
    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                KEY_PREFIX + userId,
                refreshToken,
                Duration.ofSeconds(jwtProperties.refreshTokenValiditySeconds())
        );
    }

    // 저장된 토큰과 일치하는지 검증
    public boolean matches(Long userId, String refreshToken) {
        String stored = redisTemplate.opsForValue().get(KEY_PREFIX + userId);
        return Objects.equals(stored, refreshToken);
    }

    // 폐기 (로그아웃/탈퇴 시)
    public void delete(Long userId) {
        redisTemplate.delete(KEY_PREFIX + userId);
    }
}
