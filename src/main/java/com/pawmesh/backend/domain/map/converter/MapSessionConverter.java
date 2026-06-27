package com.pawmesh.backend.domain.map.converter;

import com.pawmesh.backend.domain.map.dto.response.NearbyDogResponse;
import com.pawmesh.backend.domain.map.dto.response.PartnerLocationResponse;
import com.pawmesh.backend.domain.user.entity.Pet;
import com.pawmesh.backend.domain.walk.entity.WalkSession;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// 산책 세션 + 강아지(Pet) 데이터를 지도 조회 응답 DTO로 변환한다.
@Component
public class MapSessionConverter {

    // 주변 강아지 마커 항목 변환 (pet 이 없으면 이미지/태그는 비워둔다)
    public NearbyDogResponse toNearbyDogResponse(WalkSession session, Pet pet) {
        return new NearbyDogResponse(
                session.getId(),
                session.getDogId(),
                pet != null ? pet.getAvatarImageUrl() : null,
                toDouble(session.getCurrentLat()),
                toDouble(session.getCurrentLng()),
                session.getStatus(),
                pet != null ? pet.getPersonalityTags() : List.of());
    }

    // 파트너 위치 변환 (위치는 파트너의 활성 세션 기준)
    public PartnerLocationResponse toPartnerLocationResponse(
            Long partnerDogId, Pet pet, WalkSession partnerSession) {
        return new PartnerLocationResponse(
                partnerDogId,
                pet != null ? pet.getAvatarImageUrl() : null,
                toDouble(partnerSession.getCurrentLat()),
                toDouble(partnerSession.getCurrentLng()));
    }

    private Double toDouble(BigDecimal value) {
        return value != null ? value.doubleValue() : null;
    }
}
