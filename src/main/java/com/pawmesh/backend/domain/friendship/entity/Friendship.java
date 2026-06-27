package com.pawmesh.backend.domain.friendship.entity;

import com.pawmesh.backend.common.base.BaseEntity;
import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "friendships")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Friendship extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dog_id")
    private Pet dog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "friend_dog_id")
    private Pet friendDog;

    @Column(name = "intimacy_level", nullable = false)
    @Builder.Default
    private Integer intimacyLevel = 1;

    @Column(name = "walk_count", nullable = false)
    @Builder.Default
    private Integer walkCount = 1;

    @Column(name = "last_walked_at")
    private LocalDateTime lastWalkedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private FriendshipStatus status = FriendshipStatus.PENDING;

    // 친구 신청 생성 (산책 직후 신청하므로 lastWalkedAt 을 현재로 기록)
    public static Friendship create(Pet dog, Pet friendDog) {
        return Friendship.builder()
                .dog(dog)
                .friendDog(friendDog)
                .lastWalkedAt(LocalDateTime.now())
                .build();
    }

    // 수락 시 역방향(수락자 → 신청자) 친구 관계 생성. 양쪽 목록에 표시되도록 ACCEPTED 로 바로 생성한다.
    public static Friendship createAccepted(
            Pet dog, Pet friendDog, Integer intimacyLevel, Integer walkCount, LocalDateTime lastWalkedAt) {
        return Friendship.builder()
                .dog(dog)
                .friendDog(friendDog)
                .status(FriendshipStatus.ACCEPTED)
                .intimacyLevel(intimacyLevel)
                .walkCount(walkCount)
                .lastWalkedAt(lastWalkedAt)
                .build();
    }

    // 친구 신청 수락 (PENDING → ACCEPTED)
    public void accept() {
        validatePending();
        this.status = FriendshipStatus.ACCEPTED;
    }

    // 친구 신청 거절 (PENDING → REJECTED)
    public void reject() {
        validatePending();
        this.status = FriendshipStatus.REJECTED;
    }

    // 거절된 친구 신청 재신청 (REJECTED → PENDING, 산책 시각 갱신)
    public void reapply() {
        if (this.status != FriendshipStatus.REJECTED) {
            throw new GeneralException(ErrorStatus.FRIENDSHIP_ALREADY_EXISTS);
        }
        this.status = FriendshipStatus.PENDING;
        this.lastWalkedAt = LocalDateTime.now();
    }

    private void validatePending() {
        if (this.status != FriendshipStatus.PENDING) {
            throw new GeneralException(ErrorStatus.FRIENDSHIP_NOT_PENDING);
        }
    }
}
