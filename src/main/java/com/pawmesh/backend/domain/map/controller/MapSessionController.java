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
import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
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
// 명령(시작/위치/완료/종료)은 MapSessionCommandService 로 위임한다.
// 조회(주변/카드/파트너)는 dog 도메인 연동 전까지 더미 응답을 반환한다.
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/map-sessions")
public class MapSessionController {

    private final MapSessionCommandService mapSessionCommandService;

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

    // 주변 강아지 조회 (더미 - dog 도메인 연동 예정)
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<List<NearbyDogResponse>>> getNearbyDogs(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1.0") Double radiusKm) {
        List<NearbyDogResponse> data = List.of(
                new NearbyDogResponse(456L, 7L, "https://cdn.pawmesh.app/dogs/7/character.png",
                        37.4892100, 127.0328400, WalkSessionStatus.WALKING, List.of("소형견", "온순")),
                new NearbyDogResponse(457L, 9L, "https://cdn.pawmesh.app/dogs/9/character.png",
                        37.4885000, 127.0322000, WalkSessionStatus.MATCHED, List.of("중형견", "활발"))
        );
        return ApiResponse.success(SuccessStatus.GET_NEARBY_DOGS_SUCCESS, data);
    }

    // 산책 친구 위치 조회 (더미 - dog 도메인 연동 예정)
    @GetMapping("/{sessionId}/partner-location")
    public ResponseEntity<ApiResponse<PartnerLocationResponse>> getPartnerLocation(
            @PathVariable Long sessionId) {
        PartnerLocationResponse data = new PartnerLocationResponse(
                9L, "https://cdn.pawmesh.app/dogs/9/character.png", 37.4893500, 127.0331200);
        return ApiResponse.success(SuccessStatus.GET_PARTNER_LOCATION_SUCCESS, data);
    }
}
