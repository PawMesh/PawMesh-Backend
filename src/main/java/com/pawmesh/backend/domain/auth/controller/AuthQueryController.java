package com.pawmesh.backend.domain.auth.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.auth.dto.response.AiPhotoJobStatusResponse;
import com.pawmesh.backend.domain.auth.dto.response.CheckNicknameResponse;
import com.pawmesh.backend.domain.auth.service.AuthQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthQueryController {

    private static final String SIGNUP_TOKEN_HEADER = "X-Signup-Token";

    private final AuthQueryService authQueryService;

    // 닉네임 중복 확인
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<CheckNicknameResponse>> checkNickname(@RequestParam String nickname) {
        return ApiResponse.success(SuccessStatus.CHECK_NICKNAME_SUCCESS, authQueryService.checkNickname(nickname));
    }

    // AI 프로필 생성 상태 폴링
    @GetMapping("/signup/pet-images/ai-photos/{jobId}")
    public ResponseEntity<ApiResponse<AiPhotoJobStatusResponse>> getAiPhotoJob(
            @RequestHeader(value = SIGNUP_TOKEN_HEADER, required = false) String signupToken,
            @PathVariable String jobId
    ) {
        return ApiResponse.success(
                SuccessStatus.AI_PHOTO_JOB_SUCCESS,
                authQueryService.getAiPhotoJob(signupToken, jobId)
        );
    }
}
