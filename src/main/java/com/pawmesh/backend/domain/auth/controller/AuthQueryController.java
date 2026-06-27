package com.pawmesh.backend.domain.auth.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.auth.dto.response.AiPhotoJobStatusResponse;
import com.pawmesh.backend.domain.auth.dto.response.CheckNicknameResponse;
import com.pawmesh.backend.domain.auth.service.AuthQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth Query", description = "닉네임 중복 확인 / AI 펫 프로필 생성 상태 조회 등 인증 읽기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthQueryController {

    private static final String SIGNUP_TOKEN_HEADER = "X-Signup-Token";

    private final AuthQueryService authQueryService;

    // 닉네임 중복 확인
    @Operation(
            summary = "닉네임 중복 확인",
            description = "입력한 닉네임이 사용 가능한지(중복 여부) 확인한다."
    )
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<CheckNicknameResponse>> checkNickname(
            @Parameter(description = "중복 확인할 닉네임", example = "멍멍이집사")
            @RequestParam String nickname) {
        return ApiResponse.success(SuccessStatus.CHECK_NICKNAME_SUCCESS, authQueryService.checkNickname(nickname));
    }

    // AI 프로필 생성 상태 폴링
    @Operation(
            summary = "AI 펫 프로필 생성 상태 조회 (폴링)",
            description = "jobId로 AI 프로필 생성 작업의 상태(PROCESSING/DONE/FAILED)와 완료 시 결과를 조회한다. "
                    + "생성 시작 API 호출 후 DONE/FAILED가 될 때까지 폴링한다."
    )
    @GetMapping("/signup/pet-images/ai-photos/{jobId}")
    public ResponseEntity<ApiResponse<AiPhotoJobStatusResponse>> getAiPhotoJob(
            @Parameter(description = "사진 업로드 시 발급받은 회원가입 세션 토큰")
            @RequestHeader(value = SIGNUP_TOKEN_HEADER, required = false) String signupToken,
            @Parameter(description = "생성 시작 API가 반환한 작업 ID")
            @PathVariable String jobId
    ) {
        return ApiResponse.success(
                SuccessStatus.AI_PHOTO_JOB_SUCCESS,
                authQueryService.getAiPhotoJob(signupToken, jobId)
        );
    }
}
