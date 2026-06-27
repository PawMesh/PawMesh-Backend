package com.pawmesh.backend.domain.user.entity;

import com.pawmesh.backend.common.base.BaseEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
@Table(name = "pets")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Pet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long petId;

    // 강아지 별명
    @Column(nullable = false)
    private String petName;

    // 견종 (AI 추정 또는 보호자 편집)
    private String breed;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "pet_personality_tags", joinColumns = @JoinColumn(name = "pet_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> personalityTags = new ArrayList<>();

    // 주의사항
    @Column(columnDefinition = "TEXT")
    private String caution;

    // AI 생성 소개글
    @Column(columnDefinition = "TEXT")
    private String introText;

    // AI 생성 캐릭터 이미지 URL
    private String avatarImageUrl;

    // 업로드한 원본 사진 URL
    private String originalImageUrl;

    // 연관관계의 주인 (FK: user_id)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 강아지 프로필 부분 수정 (PATCH). null 인 필드는 변경하지 않는다.
    public void updateProfile(
            String petName,
            String breed,
            List<String> personalityTags,
            String caution,
            String introText
    ) {
        if (petName != null) {
            this.petName = petName;
        }
        if (breed != null) {
            this.breed = breed;
        }
        if (personalityTags != null) {
            this.personalityTags = personalityTags;
        }
        if (caution != null) {
            this.caution = caution;
        }
        if (introText != null) {
            this.introText = introText;
        }
    }
}
