package com.pawmesh.backend.domain.friendship.entity;

import com.pawmesh.backend.common.base.BaseEntity;
import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.friendship.enums.FriendshipStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    // TODO: Dog(Pet) 엔티티 연관관계로 전환 검토
    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "dog_id")
    // private Pet dog;
    @Column(name = "dog_id", nullable = false)
    private Long dogId;

    // TODO: Dog(Pet) 엔티티 연관관계로 전환 검토
    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "friend_dog_id")
    // private Pet friendDog;
    @Column(name = "friend_dog_id", nullable = false)
    private Long friendDogId;

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
    public static Friendship create(Long dogId, Long friendDogId) {
        return Friendship.builder()
                .dogId(dogId)
                .friendDogId(friendDogId)
                .lastWalkedAt(LocalDateTime.now())
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

    private void validatePending() {
        if (this.status != FriendshipStatus.PENDING) {
            throw new GeneralException(ErrorStatus.FRIENDSHIP_NOT_PENDING);
        }
    }
}
