package com.pawmesh.backend.domain.user.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.auth.service.RefreshTokenService;
import com.pawmesh.backend.domain.user.dto.request.UpdateDogProfileRequest;
import com.pawmesh.backend.domain.user.dto.request.UpdateHumanProfileRequest;
import com.pawmesh.backend.domain.user.dto.response.PetIdResponse;
import com.pawmesh.backend.domain.user.dto.response.UserIdResponse;
import com.pawmesh.backend.domain.user.entity.Pet;
import com.pawmesh.backend.domain.user.entity.User;
import com.pawmesh.backend.domain.user.repository.PetRepository;
import com.pawmesh.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final RefreshTokenService refreshTokenService;

    // 보호자 프로필 수정 (변경 감지로 반영)
    public UserIdResponse updateHumanProfile(Long userId, UpdateHumanProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        user.updateProfile(
                request.gender(),
                request.ageGroup(),
                request.walkStyles(),
                request.matchGender(),
                request.matchAgeFrom(),
                request.matchAgeTo()
        );
        return UserIdResponse.of(user.getUserId());
    }

    // 강아지 프로필 수정 (변경 감지로 반영)
    public PetIdResponse updateDogProfile(Long userId, UpdateDogProfileRequest request) {
        Pet pet = petRepository.findByUserUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        pet.updateProfile(
                request.petName(),
                request.breed(),
                request.personalityTags(),
                request.caution(),
                request.introText()
        );
        return PetIdResponse.of(pet.getPetId());
    }

    // 계정 삭제: 본인 Pet + User 삭제, refresh 토큰 폐기
    public UserIdResponse deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        petRepository.findByUserUserId(userId).ifPresent(petRepository::delete);
        userRepository.delete(user);
        refreshTokenService.delete(userId);
        return UserIdResponse.of(userId);
    }
}
