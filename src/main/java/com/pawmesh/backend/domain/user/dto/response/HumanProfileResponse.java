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
                user.getWalkStyles(),
                user.getMatchGender(),
                user.getMatchAgeFrom(),
                user.getMatchAgeTo()
        );
    }
}
