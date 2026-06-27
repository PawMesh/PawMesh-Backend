package com.pawmesh.backend.domain.map.controller;

import com.pawmesh.backend.common.apiPayload.ApiResponse;
import com.pawmesh.backend.domain.map.dto.CompleteWalkRequest;
import com.pawmesh.backend.domain.map.dto.CompleteWalkResponse;
import com.pawmesh.backend.domain.map.dto.DogCardResponse;
import com.pawmesh.backend.domain.map.dto.NearbyDogResponse;
import com.pawmesh.backend.domain.map.dto.PartnerLocationResponse;
import com.pawmesh.backend.domain.map.dto.StartWalkRequest;
import com.pawmesh.backend.domain.map.dto.UpdateLocationRequest;
import com.pawmesh.backend.domain.map.dto.WalkSessionIdResponse;
import com.pawmesh.backend.domain.map.enums.WalkSessionStatus;
import com.pawmesh.backend.domain.map.service.MapSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

// 지도(map) 산책 세션 API. 요청을 받아 서비스에 위임하고 ApiResponse로 감싸 반환한다.
// 산책 시작(POST)은 실제 DB 저장으로 동작하며, 나머지는 더미 응답에서 순차 교체 중.
@Tag(name = "Map Session", description = "지도 기반 산책 세션 API (산책 시작/위치/주변 조회/카드/완료/종료)")
@RestController
@RequestMapping("/v1/map-sessions")
@RequiredArgsConstructor
public class MapSessionController {

    private final MapSessionService mapSessionService;

    @Operation(summary = "산책 시작", description = "지도 진입 시 산책 세션을 생성한다. 혼자 산책 모드는 호출하지 않는다.")
    @PostMapping
    public ApiResponse<WalkSessionIdResponse> startWalk(@Valid @RequestBody StartWalkRequest request) {
        Long walkSessionId = mapSessionService.startWalk(request);
        WalkSessionIdResponse data = WalkSessionIdResponse.of(walkSessionId);
        return ApiResponse.onSuccess("WALK_201", "산책을 시작했습니다.", data);
    }

    @Operation(summary = "위치 업데이트", description = "산책 중 현재 위치를 갱신한다. 프론트가 5초마다 호출하며 WALKING/MATCHED 상태에서만 유효.")
    @PatchMapping("/{sessionId}/location")
    public ApiResponse<WalkSessionIdResponse> updateLocation(
            @Parameter(description = "산책 세션 ID", example = "123") @PathVariable Long sessionId,
            @Valid @RequestBody UpdateLocationRequest request) {
        Long walkSessionId = mapSessionService.updateLocation(sessionId, request);
        WalkSessionIdResponse data = WalkSessionIdResponse.of(walkSessionId);
        return ApiResponse.onSuccess("WALK_200", "위치를 업데이트했습니다.", data);
    }

    @Operation(summary = "주변 강아지 조회", description = "내 위치 기준 반경 내 산책 중인 강아지 마커 목록을 조회한다. (나 자신/차단/종료 세션 제외)")
    @GetMapping("/nearby")
    public ApiResponse<List<NearbyDogResponse>> getNearbyDogs(
            @Parameter(description = "기준 위도", example = "37.4889432") @RequestParam Double lat,
            @Parameter(description = "기준 경도", example = "127.0325871") @RequestParam Double lng,
            @Parameter(description = "조회 반경(km), 기본 1.0", example = "1.0")
            @RequestParam(defaultValue = "1.0") Double radiusKm) {
        // TODO(DB): bounding box + Haversine 으로 반경 내 산책 세션 조회
        List<NearbyDogResponse> data = List.of(
                new NearbyDogResponse(456L, 7L,
                        "https://cdn.pawmesh.app/dogs/7/character.png",
                        37.4892100, 127.0328400, WalkSessionStatus.WALKING,
                        List.of("소형견", "온순")),
                new NearbyDogResponse(457L, 9L,
                        "https://cdn.pawmesh.app/dogs/9/character.png",
                        37.4885000, 127.0322000, WalkSessionStatus.MATCHED,
                        List.of("중형견", "활발"))
        );
        return ApiResponse.onSuccess("WALK_200", "주변 강아지를 조회했습니다.", data);
    }

    @Operation(summary = "강아지 카드 조회", description = "지도 마커를 탭했을 때 보여줄 강아지 상세 카드를 조회한다.")
    @GetMapping("/{sessionId}/dog-card")
    public ApiResponse<DogCardResponse> getDogCard(
            @Parameter(description = "산책 세션 ID", example = "456") @PathVariable Long sessionId) {
        // TODO(DB): 세션 -> 강아지/보호자/태그/친밀도 조인 조회
        DogCardResponse data = new DogCardResponse(
                7L, "콩이", "포메라니안", "소형견",
                "https://cdn.pawmesh.app/dogs/7/character.png",
                List.of("소형견", "온순"),
                List.of("천천히 걷기", "공원 선호"),
                WalkSessionStatus.WALKING,
                "낯선 사람을 경계해요", 1);
        return ApiResponse.onSuccess("WALK_200", "강아지 카드를 조회했습니다.", data);
    }

    @Operation(summary = "산책 친구 위치 조회", description = "매칭(MATCHED)된 산책 친구의 현재 위치를 조회한다. 프론트가 5초마다 호출. MATCHED 가 아니면 404.")
    @GetMapping("/{sessionId}/partner-location")
    public ApiResponse<PartnerLocationResponse> getPartnerLocation(
            @Parameter(description = "산책 세션 ID", example = "123") @PathVariable Long sessionId) {
        // TODO(DB): MATCHED 검증 후 partner_dog_id 의 위치 반환, 아니면 404
        PartnerLocationResponse data = new PartnerLocationResponse(
                9L, "https://cdn.pawmesh.app/dogs/9/character.png",
                37.4893500, 127.0331200);
        return ApiResponse.onSuccess("WALK_200", "산책 친구 위치를 조회했습니다.", data);
    }

    @Operation(summary = "산책 완료", description = "거리/시간/전체 경로를 저장하며 산책을 완료한다. 완료 시 세션은 자동 종료되어 별도 삭제 호출이 필요 없다.")
    @PatchMapping("/{sessionId}/complete")
    public ApiResponse<CompleteWalkResponse> completeWalk(
            @Parameter(description = "산책 세션 ID", example = "123") @PathVariable Long sessionId,
            @Valid @RequestBody CompleteWalkRequest request) {
        CompleteWalkResponse data = mapSessionService.completeWalk(sessionId, request);
        return ApiResponse.onSuccess("WALK_200", "산책을 완료했습니다.", data);
    }

    @Operation(summary = "지도 삭제(종료)", description = "지도에서 나갈 때 산책 세션을 종료한다. 호출하지 않으면 마커가 사라지지 않는다.")
    @DeleteMapping("/{sessionId}")
    public ApiResponse<WalkSessionIdResponse> deleteWalk(
            @Parameter(description = "산책 세션 ID", example = "123") @PathVariable Long sessionId) {
        Long walkSessionId = mapSessionService.endWalk(sessionId);
        WalkSessionIdResponse data = WalkSessionIdResponse.of(walkSessionId);
        return ApiResponse.onSuccess("WALK_200", "산책 세션을 종료했습니다.", data);
    }
}
