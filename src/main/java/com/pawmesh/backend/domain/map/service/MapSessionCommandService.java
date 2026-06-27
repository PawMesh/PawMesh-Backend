package com.pawmesh.backend.domain.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.map.dto.request.CompleteWalkRequest;
import com.pawmesh.backend.domain.map.dto.request.StartWalkRequest;
import com.pawmesh.backend.domain.map.dto.request.UpdateLocationRequest;
import com.pawmesh.backend.domain.map.dto.response.CompleteWalkResponse;
import com.pawmesh.backend.domain.map.dto.response.WalkSessionIdResponse;
import com.pawmesh.backend.domain.walk.entity.WalkSession;
import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
import com.pawmesh.backend.domain.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// 지도(map) 명령(Command) 서비스: 산책 세션을 생성/수정/종료한다. (CQRS - 쓰기 담당)
@Service
@RequiredArgsConstructor
@Transactional
public class MapSessionCommandService {

    private final WalkSessionRepository walkSessionRepository;
    private final ObjectMapper objectMapper;

    // 산책 시작: 새 세션을 저장하고 생성된 id를 반환한다.
    public WalkSessionIdResponse startWalk(StartWalkRequest request) {
        WalkSession walkSession = WalkSession.builder()
                .dogId(request.dogId())
                .currentLat(BigDecimal.valueOf(request.currentLat()))
                .currentLng(BigDecimal.valueOf(request.currentLng()))
                .build();
        return WalkSessionIdResponse.of(walkSessionRepository.save(walkSession).getId());
    }

    // 위치 업데이트: 종료되지 않은 세션의 현재 위치를 갱신한다.
    public WalkSessionIdResponse updateLocation(Long sessionId, UpdateLocationRequest request) {
        WalkSession session = findSession(sessionId);
        if (session.getStatus() == WalkSessionStatus.ENDED) {
            throw new GeneralException(ErrorStatus.WALK_SESSION_ALREADY_ENDED);
        }
        session.updateLocation(
                BigDecimal.valueOf(request.currentLat()),
                BigDecimal.valueOf(request.currentLng()));
        return WalkSessionIdResponse.of(session.getId());
    }

    // 산책 완료: 거리/시간/경로를 저장하고 세션을 종료한다.
    public CompleteWalkResponse completeWalk(Long sessionId, CompleteWalkRequest request) {
        WalkSession session = findSession(sessionId);
        session.complete(request.distanceM(), request.durationSec(), toJson(request.routePath()));
        return CompleteWalkResponse.of(session.getId(), session.getDistanceM(), session.getDurationSec());
    }

    // 지도 삭제(종료): 세션을 종료 상태로 전환한다.
    public WalkSessionIdResponse endWalk(Long sessionId) {
        WalkSession session = findSession(sessionId);
        session.end();
        return WalkSessionIdResponse.of(session.getId());
    }

    // 세션 조회 (없으면 예외)
    private WalkSession findSession(Long sessionId) {
        return walkSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WALK_SESSION_NOT_FOUND));
    }

    // route_path(좌표 배열)를 JSON 컬럼 저장용 문자열로 변환한다.
    private String toJson(List<List<Double>> routePath) {
        try {
            return objectMapper.writeValueAsString(routePath);
        } catch (JsonProcessingException e) {
            throw new GeneralException(ErrorStatus.INVALID_ROUTE_PATH);
        }
    }
}
