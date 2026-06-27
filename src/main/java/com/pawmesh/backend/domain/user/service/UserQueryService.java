package com.pawmesh.backend.domain.user.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.user.dto.response.DogProfileResponse;
import com.pawmesh.backend.domain.user.dto.response.HumanProfileResponse;
import com.pawmesh.backend.domain.user.entity.Pet;
import com.pawmesh.backend.domain.user.entity.User;
import com.pawmesh.backend.domain.user.repository.PetRepository;
import com.pawmesh.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;

    // 보호자 프로필 조회
    public HumanProfileResponse getHumanProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return HumanProfileResponse.from(user);
    }

    // 강아지 프로필 조회
    public DogProfileResponse getDogProfile(Long userId) {
        Pet pet = petRepository.findByUserUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        return DogProfileResponse.from(pet);
    }
}
