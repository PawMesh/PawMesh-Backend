package com.pawmesh.backend.domain.map.converter;

import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.map.dto.response.NearbyDogResponse;
import com.pawmesh.backend.domain.map.dto.response.PartnerLocationResponse;
import com.pawmesh.backend.domain.walk.entity.WalkSession;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

// 산책 세션 + 강아지(Pet) 데이터를 지도 조회 응답 DTO로 변환한다.
@Component
public class MapSessionConverter {

    // 주변 강아지 마커 항목 변환 (강아지 정보는 세션의 dog 연관관계에서 가져온다)
    public NearbyDogResponse toNearbyDogResponse(WalkSession session) {
        Pet dog = session.getDog();
        return new NearbyDogResponse(
                session.getId(),
                dog.getPetId(),
                dog.getAvatarImageUrl(),
                toDouble(session.getCurrentLat()),
                toDouble(session.getCurrentLng()),
                session.getStatus(),
                dog.getPersonalityTags());
    }

    // 파트너 위치 변환 (파트너의 활성 세션 기준)
    public PartnerLocationResponse toPartnerLocationResponse(WalkSession partnerSession) {
        Pet dog = partnerSession.getDog();
        return new PartnerLocationResponse(
                dog.getPetId(),
                dog.getAvatarImageUrl(),
                toDouble(partnerSession.getCurrentLat()),
                toDouble(partnerSession.getCurrentLng()));
    }

    private Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
