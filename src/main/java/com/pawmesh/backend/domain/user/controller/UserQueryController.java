package com.pawmesh.backend.domain.user.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.user.dto.response.DogProfileResponse;
import com.pawmesh.backend.domain.user.dto.response.HumanProfileResponse;
import com.pawmesh.backend.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserQueryController {

    private final UserQueryService userQueryService;

    // 보호자 프로필 조회
    @GetMapping("/profile/human")
    public ResponseEntity<ApiResponse<HumanProfileResponse>> getHumanProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(
                SuccessStatus.GET_HUMAN_PROFILE_SUCCESS, userQueryService.getHumanProfile(userId));
    }

    // 강아지 프로필 조회
    @GetMapping("/profile/dog")
    public ResponseEntity<ApiResponse<DogProfileResponse>> getDogProfile(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(
                SuccessStatus.GET_DOG_PROFILE_SUCCESS, userQueryService.getDogProfile(userId));
    }
}
