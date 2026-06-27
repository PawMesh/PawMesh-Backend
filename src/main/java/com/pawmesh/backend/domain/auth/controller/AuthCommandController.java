package com.pawmesh.backend.domain.auth.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.auth.dto.request.AiPhotoGenerateRequest;
import com.pawmesh.backend.domain.auth.dto.request.LoginRequest;
import com.pawmesh.backend.domain.auth.dto.request.ReissueRequest;
import com.pawmesh.backend.domain.auth.dto.request.SignupCompleteRequest;
import com.pawmesh.backend.domain.auth.dto.response.AiPhotoJobResponse;
import com.pawmesh.backend.domain.auth.dto.response.CreateUserResponse;
import com.pawmesh.backend.domain.auth.dto.response.LoginResponse;
import com.pawmesh.backend.domain.auth.dto.response.UploadPetImageResponse;
import com.pawmesh.backend.domain.auth.service.AuthCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Auth Command", description = "회원가입(사진 업로드 → AI 생성 → 완료) / 로그인 / 토큰 재발급 등 인증 쓰기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthCommandController {

    private static final String SIGNUP_TOKEN_HEADER = "X-Signup-Token";

    private final AuthCommandService authCommandService;

    // 강아지 사진 업로드 → signupToken 발급
    @Operation(
            summary = "강아지 사진 업로드",
            description = "강아지 원본 사진을 S3에 업로드하고 회원가입 세션 토큰(signupToken, TTL 30분)을 발급한다. "
                    + "이후 AI 프로필 생성 / 회원가입 완료 API에서 이 토큰을 사용한다. PNG/JPG, 25MB 미만만 허용."
    )
    @PostMapping(value = "/signup/pet-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadPetImageResponse>> uploadPetImage(
            @Parameter(description = "강아지 원본 이미지 파일 (PNG/JPG, 25MB 미만)")
            @RequestPart("image") MultipartFile image
    ) {
        return ApiResponse.success(SuccessStatus.UPLOAD_PET_IMAGE_SUCCESS, authCommandService.uploadPetImage(image));
    }

    // AI 프로필 생성 시작 (비동기) → jobId 반환, 202 ACCEPTED
    @Operation(
            summary = "AI 펫 프로필 생성 시작 (비동기)",
            description = "업로드된 원본 사진으로 AI 캐릭터 아바타 + 견종/성격/소개글 생성을 비동기로 시작하고 jobId를 즉시 반환한다(202 Accepted). "
                    + "실제 생성 완료 여부는 상태 조회(폴링) API로 확인한다."
    )
    @PostMapping("/signup/pet-images/ai-photos")
    public ResponseEntity<ApiResponse<AiPhotoJobResponse>> startAiPhotoGeneration(
            @Parameter(description = "사진 업로드 시 발급받은 회원가입 세션 토큰")
            @RequestHeader(value = SIGNUP_TOKEN_HEADER, required = false) String signupToken,
            @RequestBody(required = false) AiPhotoGenerateRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.AI_PHOTO_JOB_ACCEPTED,
                authCommandService.startAiPhotoGeneration(signupToken, request)
        );
    }

    // 회원가입 완료 → userId 반환, 201 CREATED
    @Operation(
            summary = "회원가입 완료",
            description = "회원가입 세션 토큰과 입력 정보(닉네임/비밀번호 등)로 User와 Pet을 생성하고 userId를 반환한다(201 Created). "
                    + "닉네임이 이미 사용 중이면 실패한다."
    )
    @PostMapping("/signup/complete")
    public ResponseEntity<ApiResponse<CreateUserResponse>> completeSignup(
            @Parameter(description = "사진 업로드 시 발급받은 회원가입 세션 토큰")
            @RequestHeader(value = SIGNUP_TOKEN_HEADER, required = false) String signupToken,
            @Valid @RequestBody SignupCompleteRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.CREATE_USER_SUCCESS,
                authCommandService.completeSignup(signupToken, request)
        );
    }

    // 로그인 → access/refresh 토큰 발급
    @Operation(
            summary = "로그인",
            description = "닉네임/비밀번호로 인증하고 access/refresh 토큰과 userId를 발급한다."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, authCommandService.login(request));
    }

    // 토큰 재발급
    @Operation(
            summary = "토큰 재발급",
            description = "refresh 토큰을 검증(저장값 일치)해 새로운 access/refresh 토큰을 발급한다(refresh 회전)."
    )
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(
            @Valid @RequestBody ReissueRequest request
    ) {
        return ApiResponse.success(SuccessStatus.REISSUE_TOKEN_SUCCESS, authCommandService.reissue(request));
    }
}
