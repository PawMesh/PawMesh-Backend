package com.pawmesh.backend.domain.map.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.map.converter.MapSessionConverter;
import com.pawmesh.backend.domain.map.dto.response.NearbyDogResponse;
import com.pawmesh.backend.domain.map.dto.response.PartnerLocationResponse;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.dog.repository.PetRepository;
import com.pawmesh.backend.domain.walk.entity.WalkSession;
import com.pawmesh.backend.domain.walk.enums.WalkSessionStatus;
import com.pawmesh.backend.domain.walk.repository.WalkSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

// 지도(map) 조회(Query) 서비스: 주변 강아지/파트너 위치를 읽는다. (CQRS - 읽기 담당)
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MapSessionQueryService {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double KM_PER_LAT_DEGREE = 111.0;

    private final WalkSessionRepository walkSessionRepository;
    private final PetRepository petRepository;
    private final MapSessionConverter mapSessionConverter;

    // 주변 강아지 조회: bounding box 1차 필터 후 Haversine 거리로 반경 내만 추려 거리순 정렬한다.
    public List<NearbyDogResponse> getNearbyDogs(double lat, double lng, double radiusKm) {
        double latDelta = radiusKm / KM_PER_LAT_DEGREE;
        double lngDelta = radiusKm / (KM_PER_LAT_DEGREE * Math.cos(Math.toRadians(lat)));

        List<WalkSession> candidates = walkSessionRepository.findActiveWithinBox(
                BigDecimal.valueOf(lat - latDelta),
                BigDecimal.valueOf(lat + latDelta),
                BigDecimal.valueOf(lng - lngDelta),
                BigDecimal.valueOf(lng + lngDelta));

        return candidates.stream()
                .filter(session -> distanceKm(lat, lng,
                        session.getCurrentLat().doubleValue(),
                        session.getCurrentLng().doubleValue()) <= radiusKm)
                .sorted(Comparator.comparingDouble(session -> distanceKm(lat, lng,
                        session.getCurrentLat().doubleValue(),
                        session.getCurrentLng().doubleValue())))
                .map(session -> mapSessionConverter.toNearbyDogResponse(session, findPet(session.getDog().getPetId())))
                .toList();
    }

    // 파트너 위치 조회: MATCHED 세션의 파트너 강아지 현재 위치를 반환한다.
    public PartnerLocationResponse getPartnerLocation(Long sessionId) {
        WalkSession session = walkSessionRepository.findById(sessionId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.WALK_SESSION_NOT_FOUND));

        if (session.getStatus() != WalkSessionStatus.MATCHED || session.getPartnerDog() == null) {
            throw new GeneralException(ErrorStatus.PARTNER_NOT_MATCHED);
        }

        Long partnerDogId = session.getPartnerDog().getPetId();
        WalkSession partnerSession = walkSessionRepository
                .findFirstByDog_PetIdAndStatusNotOrderByStartedAtDesc(partnerDogId, WalkSessionStatus.ENDED)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTNER_NOT_MATCHED));

        return mapSessionConverter.toPartnerLocationResponse(partnerDogId, findPet(partnerDogId), partnerSession);
    }

    // 강아지(Pet) 조회. 없으면 null (이미지/태그 없이 응답)
    private Pet findPet(Long dogId) {
        return petRepository.findById(dogId).orElse(null);
    }

    // 두 좌표 사이 거리(km) - Haversine
    private double distanceKm(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
