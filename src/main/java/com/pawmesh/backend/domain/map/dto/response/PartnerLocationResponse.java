package com.pawmesh.backend.domain.map.dto.response;

// 산책 친구(파트너) 위치 응답.
public record PartnerLocationResponse(
        Long partnerDogId,
        String characterImageUrl,
        Double lat,
        Double lng
) {
}
