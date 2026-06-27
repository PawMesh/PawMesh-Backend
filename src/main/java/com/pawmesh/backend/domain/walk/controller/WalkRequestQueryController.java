package com.pawmesh.backend.domain.walk.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.walk.dto.response.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.service.WalkRequestQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/walk-requests")
public class WalkRequestQueryController {

    private final WalkRequestQueryService walkRequestQueryService;

    // 받은 산책 요청 조회
    @GetMapping("/received")
    public ResponseEntity<ApiResponse<List<WalkRequestResponse>>> getReceivedWalkRequests(
            // TODO: 인증된 사용자의 강아지 ID 로 대체
            @RequestParam Long receiverDogId
    ) {
        return ApiResponse.success(
                SuccessStatus.GET_RECEIVED_WALK_REQUESTS_SUCCESS,
                walkRequestQueryService.getReceived(receiverDogId));
    }
}
