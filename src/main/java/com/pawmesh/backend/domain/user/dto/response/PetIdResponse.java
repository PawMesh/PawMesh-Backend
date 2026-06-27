package com.pawmesh.backend.domain.user.dto.response;

public record PetIdResponse(
        Long petId
) {
    public static PetIdResponse of(Long petId) {
        return new PetIdResponse(petId);
    }
}
