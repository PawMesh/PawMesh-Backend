package com.pawmesh.backend.domain.walk.repository;

import com.pawmesh.backend.domain.walk.entity.WalkSession;
import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// 산책 세션(WALK_SESSIONS) 저장소. JpaRepository 상속으로 기본 CRUD 자동 제공.
public interface WalkSessionRepository extends JpaRepository<WalkSession, Long> {

    // 종료되지 않은 세션 중 위경도 bounding box 안에 있는 것들 (1차 필터, 정밀 거리는 서비스에서)
    @Query("""
            select w from WalkSession w
            where w.status <> com.pawmesh.backend.domain.walk.enums.WalkSessionStatus.ENDED
              and w.currentLat between :minLat and :maxLat
              and w.currentLng between :minLng and :maxLng
            """)
    List<WalkSession> findActiveWithinBox(
            @Param("minLat") BigDecimal minLat,
            @Param("maxLat") BigDecimal maxLat,
            @Param("minLng") BigDecimal minLng,
            @Param("maxLng") BigDecimal maxLng);

    // 특정 강아지의 가장 최근 활성 세션 (파트너 현재 위치 조회용)
    Optional<WalkSession> findFirstByDog_PetIdAndStatusNotOrderByStartedAtDesc(
            Long dogId, WalkSessionStatus status);
}
