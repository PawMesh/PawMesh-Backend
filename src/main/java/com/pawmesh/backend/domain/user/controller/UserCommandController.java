package com.pawmesh.backend.domain.user.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.user.dto.request.UpdateDogProfileRequest;
import com.pawmesh.backend.domain.user.dto.request.UpdateHumanProfileRequest;
import com.pawmesh.backend.domain.user.dto.response.PetIdResponse;
import com.pawmesh.backend.domain.user.dto.response.UserIdResponse;
import com.pawmesh.backend.domain.user.service.UserCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Command", description = "보호자/강아지 프로필 수정 및 계정 삭제 API (로그인 필요)")
@SecurityRequirement(name = "BearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserCommandController {

    private final UserCommandService userCommandService;

    // 보호자 프로필 수정 → userId 반환
    @Operation(
            summary = "보호자 프로필 수정",
            description = "로그인한 사용자(보호자)의 프로필을 수정하고 userId를 반환한다. 액세스 토큰 필요."
    )
    @PatchMapping("/profile/human")
    public ResponseEntity<ApiResponse<UserIdResponse>> updateHumanProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdateHumanProfileRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.UPDATE_HUMAN_PROFILE_SUCCESS,
                userCommandService.updateHumanProfile(userId, request));
    }

    // 강아지 프로필 수정 → petId 반환
    @Operation(
            summary = "강아지 프로필 수정",
            description = "로그인한 사용자의 강아지 프로필을 수정하고 petId를 반환한다. 액세스 토큰 필요."
    )
    @PatchMapping("/profile/dog")
    public ResponseEntity<ApiResponse<PetIdResponse>> updateDogProfile(
            @AuthenticationPrincipal Long userId,
            @RequestBody UpdateDogProfileRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.UPDATE_DOG_PROFILE_SUCCESS,
                userCommandService.updateDogProfile(userId, request));
    }

    // 계정 삭제 → userId 반환
    @Operation(
            summary = "계정 삭제(회원 탈퇴)",
            description = "로그인한 사용자의 계정을 삭제하고 userId를 반환한다. 액세스 토큰 필요."
    )
    @DeleteMapping
    public ResponseEntity<ApiResponse<UserIdResponse>> deleteAccount(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(
                SuccessStatus.DELETE_USER_SUCCESS, userCommandService.deleteAccount(userId));
    }
}
