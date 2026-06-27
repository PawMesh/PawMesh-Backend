package com.pawmesh.backend.domain.user.dto.response;

import com.pawmesh.backend.domain.dog.entity.Pet;
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
                // LAZY @ElementCollection — 트랜잭션 안에서 복사해 초기화(직렬화 시 LazyInitializationException 방지)
                pet.getPersonalityTags() == null ? List.of() : List.copyOf(pet.getPersonalityTags()),
                pet.getCaution(),
                pet.getIntroText(),
                pet.getAvatarImageUrl(),
                pet.getOriginalImageUrl()
        );
    }
}
