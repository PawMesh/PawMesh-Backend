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

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthCommandController {

    private static final String SIGNUP_TOKEN_HEADER = "X-Signup-Token";

    private final AuthCommandService authCommandService;

    // 강아지 사진 업로드 → signupToken 발급
    @PostMapping(value = "/signup/pet-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadPetImageResponse>> uploadPetImage(
            @RequestPart("image") MultipartFile image
    ) {
        return ApiResponse.success(SuccessStatus.UPLOAD_PET_IMAGE_SUCCESS, authCommandService.uploadPetImage(image));
    }

    // AI 프로필 생성 시작 (비동기) → jobId 반환, 202 ACCEPTED
    @PostMapping("/signup/pet-images/ai-photos")
    public ResponseEntity<ApiResponse<AiPhotoJobResponse>> startAiPhotoGeneration(
            @RequestHeader(value = SIGNUP_TOKEN_HEADER, required = false) String signupToken,
            @RequestBody(required = false) AiPhotoGenerateRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.AI_PHOTO_JOB_ACCEPTED,
                authCommandService.startAiPhotoGeneration(signupToken, request)
        );
    }

    // 회원가입 완료 → userId 반환, 201 CREATED
    @PostMapping("/signup/complete")
    public ResponseEntity<ApiResponse<CreateUserResponse>> completeSignup(
            @RequestHeader(value = SIGNUP_TOKEN_HEADER, required = false) String signupToken,
            @Valid @RequestBody SignupCompleteRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.CREATE_USER_SUCCESS,
                authCommandService.completeSignup(signupToken, request)
        );
    }

    // 로그인 → access/refresh 토큰 발급
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ApiResponse.success(SuccessStatus.LOGIN_SUCCESS, authCommandService.login(request));
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<LoginResponse>> reissue(
            @Valid @RequestBody ReissueRequest request
    ) {
        return ApiResponse.success(SuccessStatus.REISSUE_TOKEN_SUCCESS, authCommandService.reissue(request));
    }
}
