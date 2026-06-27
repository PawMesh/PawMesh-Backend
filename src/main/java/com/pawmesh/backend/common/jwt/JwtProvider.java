package com.pawmesh.backend.common.jwt;

import com.pawmesh.backend.common.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// JWT(access/refresh) 발급 및 검증.
@Slf4j
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey key;

    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes(StandardCharsets.UTF_8));
    }

    // access 토큰 발급
    public String createAccessToken(Long userId) {
        return createToken(userId, jwtProperties.accessTokenValiditySeconds());
    }

    // refresh 토큰 발급
    public String createRefreshToken(Long userId) {
        return createToken(userId, jwtProperties.refreshTokenValiditySeconds());
    }

    // 토큰에서 userId 추출
    public Long getUserId(String token) {
        Claims claims = parse(token);
        return Long.valueOf(claims.getSubject());
    }

    // 토큰 유효성 검증 (서명/만료)
    public boolean validate(String token) {
        try {
            parse(token);
            return true;
        } catch (Exception e) {
            log.debug("[*] Invalid JWT : {}", e.getMessage());
            return false;
        }
    }

    private String createToken(Long userId, long validitySeconds) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(validitySeconds);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
