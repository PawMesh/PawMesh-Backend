package com.pawmesh.backend.domain.walk.controller;

import com.pawmesh.backend.domain.walk.dto.WalkRequestCreateRequest;
import com.pawmesh.backend.domain.walk.dto.WalkRequestResponse;
import com.pawmesh.backend.domain.walk.service.WalkRequestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/walk-requests")
public class WalkRequestController {

    private final WalkRequestService walkRequestService;

    // TODO : 공통응답 구현되면 수정

    /** 산책 요청 보내기 */
    @PostMapping
    public ResponseEntity<WalkRequestResponse> createWalkRequest(
            @Valid @RequestBody WalkRequestCreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walkRequestService.create(request));
    }

    /** 받은 산책 요청 조회 */
    @GetMapping("/received")
    public ResponseEntity<List<WalkRequestResponse>> getReceivedWalkRequests(
            // TODO: 인증된 사용자의 강아지 ID 로 대체
            @RequestParam Long receiverDogId
    ) {
        return ResponseEntity.ok(walkRequestService.getReceived(receiverDogId));
    }

    /** 산책 요청 수락 */
    @PatchMapping("/{id}/accept")
    public ResponseEntity<WalkRequestResponse> acceptWalkRequest(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(walkRequestService.accept(id));
    }

    /** 산책 요청 거절 */
    @PatchMapping("/{id}/reject")
    public ResponseEntity<WalkRequestResponse> rejectWalkRequest(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(walkRequestService.reject(id));
    }

    /** 산책 요청 취소 */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<WalkRequestResponse> cancelWalkRequest(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(walkRequestService.cancel(id));
    }
}
