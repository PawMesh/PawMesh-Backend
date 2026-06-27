package com.pawmesh.backend.domain.friendship.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.friendship.dto.request.FriendshipCreateRequest;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipAcceptResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipRejectResponse;
import com.pawmesh.backend.domain.friendship.dto.response.FriendshipResponse;
import com.pawmesh.backend.domain.friendship.service.FriendshipCommandService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/friendships")
@SecurityRequirement(name = "BearerAuth")
public class FriendshipCommandController {

    private final FriendshipCommandService friendshipCommandService;

    // 친구 신청
    @PostMapping
    public ResponseEntity<ApiResponse<FriendshipResponse>> createFriendship(
            @Valid @RequestBody FriendshipCreateRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.CREATE_FRIENDSHIP_SUCCESS,
                friendshipCommandService.create(request));
    }

    // 친구 신청 수락
    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<FriendshipAcceptResponse>> acceptFriendship(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                SuccessStatus.ACCEPT_FRIENDSHIP_SUCCESS,
                friendshipCommandService.accept(id));
    }

    // 친구 신청 거절
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<FriendshipRejectResponse>> rejectFriendship(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                SuccessStatus.REJECT_FRIENDSHIP_SUCCESS,
                friendshipCommandService.reject(id));
    }
}
