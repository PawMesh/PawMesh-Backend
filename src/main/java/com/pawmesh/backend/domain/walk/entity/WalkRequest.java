package com.pawmesh.backend.domain.walk.entity;

import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "WALK_REQUESTS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: BaseEntity(협업자 작업 중) 완성 후 `extends BaseEntity`로 전환하여 created_at 상속
public class WalkRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: WalkSession 연관관계로 전환 검토
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "walk_session_id")
    // private WalkSession walkSession;
    @Column(name = "walk_session_id")
    private Long walkSessionId;

    // TODO: Dog 엔티티 생성 후 연관관계로 전환
    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "requester_dog_id")
    // private Dog requesterDog;
    @Column(name = "requester_dog_id", nullable = false)
    private Long requesterDogId;

    // TODO: Dog 엔티티 생성 후 연관관계로 전환
    // @ManyToOne(fetch = FetchType.LAZY, optional = false)
    // @JoinColumn(name = "receiver_dog_id")
    // private Dog receiverDog;
    @Column(name = "receiver_dog_id", nullable = false)
    private Long receiverDogId;

    @Column(name = "message", length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalkRequestStatus status;

    // created_at 은 BaseEntity 에서 상속 예정
    // @CreationTimestamp
    // @Column(name = "created_at", nullable = false, updatable = false)
    // private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Builder
    private WalkRequest(Long requesterDogId, Long receiverDogId, String message) {
        this.requesterDogId = requesterDogId;
        this.receiverDogId = receiverDogId;
        this.message = message;
        this.status = WalkRequestStatus.PENDING;
    }

    public void accept() {
        changeStatus(WalkRequestStatus.ACCEPTED);
    }

    public void reject() {
        changeStatus(WalkRequestStatus.REJECTED);
    }

    public void cancel() {
        changeStatus(WalkRequestStatus.CANCELED);
    }

    private void changeStatus(WalkRequestStatus next) {
        // TODO: 커스텀 예외(global/apiPayload)로 교체 검토
        if (this.status != WalkRequestStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 산책 요청입니다. 현재 상태: " + this.status);
        }
        this.status = next;
        this.respondedAt = LocalDateTime.now();
    }
}
