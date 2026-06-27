package com.pawmesh.backend.domain.friendship.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipListResponse;
import com.pawmesh.backend.domain.friendship.service.FriendshipQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/friendships")
public class FriendshipQueryController {

    private final FriendshipQueryService friendshipQueryService;

    // 친구 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<FriendshipListResponse>> getFriendships(
            @AuthenticationPrincipal Long userId
    ) {
        return ApiResponse.success(
                SuccessStatus.GET_FRIENDSHIPS_SUCCESS,
                friendshipQueryService.getFriendships(userId));
    }
}
