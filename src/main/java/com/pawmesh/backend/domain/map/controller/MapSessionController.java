package com.pawmesh.backend.domain.map.controller;

import com.pawmesh.backend.common.response.ApiResponse;
import com.pawmesh.backend.common.status.success.SuccessStatus;
import com.pawmesh.backend.domain.map.dto.request.CompleteWalkRequest;
import com.pawmesh.backend.domain.map.dto.request.StartWalkRequest;
import com.pawmesh.backend.domain.map.dto.request.UpdateLocationRequest;
import com.pawmesh.backend.domain.map.dto.response.CompleteWalkResponse;
import com.pawmesh.backend.domain.map.dto.response.NearbyDogResponse;
import com.pawmesh.backend.domain.map.dto.response.PartnerLocationResponse;
import com.pawmesh.backend.domain.map.dto.response.WalkSessionIdResponse;
import com.pawmesh.backend.domain.map.service.MapSessionCommandService;
import com.pawmesh.backend.domain.map.service.MapSessionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// 지도(map) 산책 세션 API.
// 명령(시작/위치/완료/종료)은 CommandService, 조회(주변/파트너)는 QueryService 로 위임한다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/map-sessions")
public class MapSessionController {

    private final MapSessionCommandService mapSessionCommandService;
    private final MapSessionQueryService mapSessionQueryService;

    // 산책 시작 → walkSessionId 반환
    @PostMapping
    public ResponseEntity<ApiResponse<WalkSessionIdResponse>> startWalk(
            @RequestBody StartWalkRequest request) {
        return ApiResponse.success(SuccessStatus.START_WALK_SUCCESS, mapSessionCommandService.startWalk(request));
    }

    // 위치 업데이트 → walkSessionId 반환
    @PatchMapping("/{sessionId}/location")
    public ResponseEntity<ApiResponse<WalkSessionIdResponse>> updateLocation(
            @PathVariable Long sessionId,
            @RequestBody UpdateLocationRequest request) {
        return ApiResponse.success(
                SuccessStatus.UPDATE_LOCATION_SUCCESS,
                mapSessionCommandService.updateLocation(sessionId, request));
    }

    // 산책 완료 → 거리/시간 반환
    @PatchMapping("/{sessionId}/complete")
    public ResponseEntity<ApiResponse<CompleteWalkResponse>> completeWalk(
            @PathVariable Long sessionId,
            @RequestBody CompleteWalkRequest request) {
        return ApiResponse.success(
                SuccessStatus.COMPLETE_WALK_SUCCESS,
                mapSessionCommandService.completeWalk(sessionId, request));
    }

    // 지도 삭제(종료) → walkSessionId 반환
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<ApiResponse<WalkSessionIdResponse>> endWalk(
            @PathVariable Long sessionId) {
        return ApiResponse.success(SuccessStatus.END_WALK_SUCCESS, mapSessionCommandService.endWalk(sessionId));
    }

    // 주변 강아지 조회 → 반경 내 산책 중인 강아지 목록
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<NearbyDogResponse>>> getNearbyDogs(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1.0") Double radiusKm) {
        return ApiResponse.success(
                SuccessStatus.GET_NEARBY_DOGS_SUCCESS,
                mapSessionQueryService.getNearbyDogs(lat, lng, radiusKm));
    }

    // 산책 친구 위치 조회 → 매칭된 파트너 강아지 현재 위치
    @GetMapping("/{sessionId}/partner-location")
    public ResponseEntity<ApiResponse<PartnerLocationResponse>> getPartnerLocation(
            @PathVariable Long sessionId) {
        return ApiResponse.success(
                SuccessStatus.GET_PARTNER_LOCATION_SUCCESS,
                mapSessionQueryService.getPartnerLocation(sessionId));
    }
}
