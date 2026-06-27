package com.pawmesh.backend.domain.walk.entity;

import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "WALK_SESSIONS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WalkSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: Dog 엔티티 생성 후 연관관계로 전환
    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "dog_id")
    // private Dog dog;
    @Column(name = "dog_id", nullable = false)
    private Long dogId;

    // TODO: Dog 엔티티 생성 후 연관관계로 전환 (혼자 산책 시 null)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "partner_dog_id")
    // private Dog partnerDog;
    @Column(name = "partner_dog_id")
    private Long partnerDogId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalkSessionStatus status;

    @Column(name = "current_lat", nullable = false, precision = 10, scale = 7)
    private BigDecimal currentLat;

    @Column(name = "current_lng", nullable = false, precision = 10, scale = 7)
    private BigDecimal currentLng;

    @Column(name = "distance_m", nullable = false)
    private Integer distanceM;

    @Column(name = "duration_sec", nullable = false)
    private Integer durationSec;

    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "route_path", columnDefinition = "json")
    private String routePath;

    @Builder
    private WalkSession(Long dogId, Long partnerDogId, BigDecimal currentLat, BigDecimal currentLng) {
        this.dogId = dogId;
        this.partnerDogId = partnerDogId;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
        this.status = WalkSessionStatus.WALKING;
        this.distanceM = 0;
        this.durationSec = 0;
    }

    // 위치 갱신 (상태 검증은 서비스에서 수행).
    public void updateLocation(BigDecimal currentLat, BigDecimal currentLng) {
        this.currentLat = currentLat;
        this.currentLng = currentLng;
    }

    // 산책 완료: 거리/시간/경로 저장 후 종료 상태로 전환.
    public void complete(int distanceM, int durationSec, String routePath) {
        this.distanceM = distanceM;
        this.durationSec = durationSec;
        this.routePath = routePath;
        this.status = WalkSessionStatus.ENDED;
        this.endedAt = LocalDateTime.now();
    }

    // 지도 종료(소프트 삭제): 상태만 종료로 전환.
    public void end() {
        this.status = WalkSessionStatus.ENDED;
        this.endedAt = LocalDateTime.now();
    }
}