package com.pawmesh.backend.domain.user.entity;

import com.pawmesh.backend.common.base.BaseEntity;
import com.pawmesh.backend.domain.user.enums.AgeGroup;
import com.pawmesh.backend.domain.user.enums.Gender;
import com.pawmesh.backend.domain.user.enums.WalkStyle;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    // 로그인 ID 로 사용
    @Column(nullable = false, unique = true)
    private String nickname;

    // 인코딩되어 저장된다
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private AgeGroup ageGroup;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_walk_styles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "walk_style")
    @Builder.Default
    private List<WalkStyle> walkStyles = new ArrayList<>();

    // 매칭 필터 (null = 전체 허용)
    @Enumerated(EnumType.STRING)
    private Gender matchGender;

    @Enumerated(EnumType.STRING)
    private AgeGroup matchAgeFrom;

    @Enumerated(EnumType.STRING)
    private AgeGroup matchAgeTo;

    // MVP: 1 보호자 - 1 강아지. 연관관계의 주인은 Pet(FK 소유).
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Pet pet;

    // 보호자 프로필 부분 수정 (PATCH). null 인 필드는 변경하지 않는다.
    public void updateProfile(
            Gender gender,
            AgeGroup ageGroup,
            List<WalkStyle> walkStyles,
            Gender matchGender,
            AgeGroup matchAgeFrom,
            AgeGroup matchAgeTo
    ) {
        if (gender != null) {
            this.gender = gender;
        }
        if (ageGroup != null) {
            this.ageGroup = ageGroup;
        }
        if (walkStyles != null) {
            this.walkStyles = walkStyles;
        }
        if (matchGender != null) {
            this.matchGender = matchGender;
        }
        if (matchAgeFrom != null) {
            this.matchAgeFrom = matchAgeFrom;
        }
        if (matchAgeTo != null) {
            this.matchAgeTo = matchAgeTo;
        }
    }
}
