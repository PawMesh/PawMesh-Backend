package com.pawmesh.backend.domain.user.dto.request;

import com.pawmesh.backend.domain.user.enums.AgeGroup;
import com.pawmesh.backend.domain.user.enums.Gender;
import com.pawmesh.backend.domain.user.enums.WalkStyle;
import java.util.List;

// 보호자 프로필 부분 수정 요청 (PATCH). 변경할 필드만 채워 보낸다.
public record UpdateHumanProfileRequest(
        Gender gender,
        AgeGroup ageGroup,
        List<WalkStyle> walkStyles,
        Gender matchGender,
        AgeGroup matchAgeFrom,
        AgeGroup matchAgeTo
) {
}
