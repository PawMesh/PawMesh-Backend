package com.pawmesh.backend.domain.map.service;

import com.pawmesh.backend.domain.map.dto.StartWalkRequest;
import com.pawmesh.backend.domain.walk.entity.WalkSession;
import com.pawmesh.backend.domain.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

// 지도(map) 기능의 비즈니스 로직.
@Service
@RequiredArgsConstructor
public class MapSessionService {

    private final WalkSessionRepository walkSessionRepository;

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
}
