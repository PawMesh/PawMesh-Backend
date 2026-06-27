package com.pawmesh.backend.domain.walk.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.walk.dto.request.WalkRequestCreateRequest;
import com.pawmesh.backend.domain.walk.dto.response.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.service.WalkRequestCommandService;
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
@RequestMapping("/v1/walk-requests")
public class WalkRequestCommandController {

    private final WalkRequestCommandService walkRequestCommandService;

    // 산책 요청 보내기
    @PostMapping
    public ResponseEntity<ApiResponse<WalkRequestResponse>> createWalkRequest(
            @Valid @RequestBody WalkRequestCreateRequest request
    ) {
        return ApiResponse.success(
                SuccessStatus.CREATE_WALK_REQUEST_SUCCESS,
                walkRequestCommandService.create(request));
    }

    // 산책 요청 수락
    @PatchMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<WalkRequestResponse>> acceptWalkRequest(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                SuccessStatus.ACCEPT_WALK_REQUEST_SUCCESS,
                walkRequestCommandService.accept(id));
    }

    // 산책 요청 거절
    @PatchMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<WalkRequestResponse>> rejectWalkRequest(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                SuccessStatus.REJECT_WALK_REQUEST_SUCCESS,
                walkRequestCommandService.reject(id));
    }

    // 산책 요청 취소
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<WalkRequestResponse>> cancelWalkRequest(
            @PathVariable Long id
    ) {
        return ApiResponse.success(
                SuccessStatus.CANCEL_WALK_REQUEST_SUCCESS,
                walkRequestCommandService.cancel(id));
    }
}
