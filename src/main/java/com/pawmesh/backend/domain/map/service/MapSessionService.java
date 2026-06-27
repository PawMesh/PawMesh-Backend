package com.pawmesh.backend.domain.map.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawmesh.backend.domain.map.dto.CompleteWalkRequest;
import com.pawmesh.backend.domain.map.dto.CompleteWalkResponse;
import com.pawmesh.backend.domain.map.dto.StartWalkRequest;
import com.pawmesh.backend.domain.map.dto.UpdateLocationRequest;
import com.pawmesh.backend.domain.walk.entity.WalkSession;
import com.pawmesh.backend.domain.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// 지도(map) 기능의 비즈니스 로직.
@Service
@RequiredArgsConstructor
public class MapSessionService {

    private final WalkSessionRepository walkSessionRepository;
    private final ObjectMapper objectMapper;

    // 산책 시작: 새 세션을 만들어 저장하고 생성된 id를 반환한다.
    @Transactional
    public Long startWalk(StartWalkRequest request) {
        WalkSession walkSession = WalkSession.builder()
                .dogId(request.dogId())
                // 위경도는 DECIMAL(10,7) 컬럼이라 BigDecimal로 변환해 저장
                .currentLat(BigDecimal.valueOf(request.currentLat()))
                .currentLng(BigDecimal.valueOf(request.currentLng()))
                .build();

        return walkSessionRepository.save(walkSession).getId();
    }

    // 위치 업데이트: 세션을 찾아 현재 위치를 갱신한다. (WALKING/MATCHED 에서만)
    @Transactional
    public Long updateLocation(Long sessionId, UpdateLocationRequest request) {
        WalkSession session = findSession(sessionId);
        session.updateLocation(
                BigDecimal.valueOf(request.currentLat()),
                BigDecimal.valueOf(request.currentLng()));
        return session.getId();
    }

    // 산책 완료: 거리/시간/경로를 저장하고 세션을 종료한다.
    @Transactional
    public CompleteWalkResponse completeWalk(Long sessionId, CompleteWalkRequest request) {
        WalkSession session = findSession(sessionId);
        session.complete(request.distanceM(), request.durationSec(), toJson(request.routePath()));
        return new CompleteWalkResponse(session.getId(), session.getDistanceM(), session.getDurationSec());
    }

    // 지도 삭제(종료): 세션을 종료 상태로 전환한다.
    @Transactional
    public Long endWalk(Long sessionId) {
        WalkSession session = findSession(sessionId);
        session.end();
        return session.getId();
    }

    private WalkSession findSession(Long sessionId) {
        return walkSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("산책 세션을 찾을 수 없습니다. id=" + sessionId));
    }

    // route_path(좌표 배열)를 DB의 JSON 컬럼에 넣기 위해 문자열로 변환한다.
    private String toJson(List<List<Double>> routePath) {
        try {
            return objectMapper.writeValueAsString(routePath);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("경로(routePath) 변환에 실패했습니다.", e);
        }
    }
}
