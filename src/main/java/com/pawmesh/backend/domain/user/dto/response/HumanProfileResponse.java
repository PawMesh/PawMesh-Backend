package com.pawmesh.backend.domain.user.dto.response;

import com.pawmesh.backend.domain.user.entity.User;
import com.pawmesh.backend.domain.user.enums.AgeGroup;
import com.pawmesh.backend.domain.user.enums.Gender;
import com.pawmesh.backend.domain.user.enums.WalkStyle;
import java.util.List;

public record HumanProfileResponse(
        Long userId,
        String nickname,
        Gender gender,
        AgeGroup ageGroup,
        List<WalkStyle> walkStyles,
        Gender matchGender,
        AgeGroup matchAgeFrom,
        AgeGroup matchAgeTo
) {
    public static HumanProfileResponse from(User user) {
        return new HumanProfileResponse(
                user.getUserId(),
                user.getNickname(),
                user.getGender(),
                user.getAgeGroup(),
                // LAZY @ElementCollection — 트랜잭션 안에서 복사해 초기화(직렬화 시 LazyInitializationException 방지)
                user.getWalkStyles() == null ? List.of() : List.copyOf(user.getWalkStyles()),
                user.getMatchGender(),
                user.getMatchAgeFrom(),
                user.getMatchAgeTo()
        );
    }
}
