package com.pawmesh.backend.domain.user.dto.response;

import com.pawmesh.backend.domain.user.entity.Pet;
import java.util.List;

public record DogProfileResponse(
        Long petId,
        String petName,
        String breed,
        List<String> personalityTags,
        String caution,
        String introText,
        String avatarImageUrl,
        String originalImageUrl
) {
    public static DogProfileResponse from(Pet pet) {
        return new DogProfileResponse(
                pet.getPetId(),
                pet.getPetName(),
                pet.getBreed(),
                pet.getPersonalityTags(),
                pet.getCaution(),
                pet.getIntroText(),
                pet.getAvatarImageUrl(),
                pet.getOriginalImageUrl()
        );
    }
}
