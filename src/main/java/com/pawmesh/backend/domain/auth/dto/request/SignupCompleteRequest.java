package com.pawmesh.backend.domain.auth.dto.request;

import com.pawmesh.backend.domain.user.enums.AgeGroup;
import com.pawmesh.backend.domain.user.enums.Gender;
import com.pawmesh.backend.domain.user.enums.WalkStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

// 회원가입 완료 요청. avatarImageUrl/originalImageUrl 은 세션에서 가져오므로 포함하지 않는다.
public record SignupCompleteRequest(
        // 보호자
        @NotBlank(message = "닉네임은 필수입니다.")
        String nickname,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
        String password,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotNull(message = "연령대는 필수입니다.")
        AgeGroup ageGroup,

        List<WalkStyle> walkStyles,

        // 매칭 필터 (선택)
        Gender matchGender,
        AgeGroup matchAgeFrom,
        AgeGroup matchAgeTo,

        // 강아지 (AI 결과를 확정/편집해서 전달)
        @NotBlank(message = "강아지 별명은 필수입니다.")
        String petName,

        String breed,
        List<String> personalityTags,
        String caution,
        String introText
) {
}
