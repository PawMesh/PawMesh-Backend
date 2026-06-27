package com.pawmesh.backend.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 인증되지 않은 요청에 ApiResponse 형식의 401 을 반환한다.
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorStatus errorStatus = ErrorStatus.UNAUTHORIZED;
        response.setStatus(errorStatus.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ApiResponse<Void> body = new ApiResponse<>(
                false, errorStatus.getCode(), errorStatus.getMessage(), null);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
