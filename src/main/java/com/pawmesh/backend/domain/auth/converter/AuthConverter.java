package com.pawmesh.backend.domain.auth.converter;

import com.pawmesh.backend.domain.auth.dto.request.SignupCompleteRequest;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.user.entity.User;
import com.pawmesh.backend.domain.user.enums.WalkStyle;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

// 회원가입 완료 요청/세션 데이터를 엔티티로 변환한다.
@Component
public class AuthConverter {

    // 보호자(User) 생성. password 는 이미 인코딩된 값을 받는다.
    public User toUser(SignupCompleteRequest request, String encodedPassword) {
        return User.builder()
                .nickname(request.nickname())
                .password(encodedPassword)
                .gender(request.gender())
                .ageGroup(request.ageGroup())
                .walkStyles(nullSafe(request.walkStyles()))
                .matchGender(request.matchGender())
                .matchAgeFrom(request.matchAgeFrom())
                .matchAgeTo(request.matchAgeTo())
                .build();
    }

    // 강아지(Pet) 생성. 이미지 URL 은 세션에서 가져온 값을 받는다.
    public Pet toPet(SignupCompleteRequest request, User user, String originalImageUrl, String avatarImageUrl) {
        return Pet.builder()
                .petName(request.petName())
                .breed(request.breed())
                .personalityTags(nullSafeStrings(request.personalityTags()))
                .caution(request.caution())
                .introText(request.introText())
                .originalImageUrl(originalImageUrl)
                .avatarImageUrl(avatarImageUrl)
                .user(user)
                .build();
    }

    private List<WalkStyle> nullSafe(List<WalkStyle> values) {
        return (values != null) ? values : new ArrayList<>();
    }

    private List<String> nullSafeStrings(List<String> values) {
        return (values != null) ? values : new ArrayList<>();
    }
}
