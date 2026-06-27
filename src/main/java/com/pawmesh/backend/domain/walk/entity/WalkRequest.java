package com.pawmesh.backend.domain.walk.entity;

import com.pawmesh.backend.common.base.BaseEntity;
import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.domain.dog.entity.Pet;
import com.pawmesh.backend.domain.walk.enums.WalkRequestStatus;
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
@Table(name = "walk_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WalkRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_session_id")
    private WalkSession walkSession;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requester_dog_id")
    private Pet requesterDog;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_dog_id")
    private Pet receiverDog;

    @Column(name = "message", length = 255)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private WalkRequestStatus status = WalkRequestStatus.PENDING;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    // 산책 요청 생성 (요청자의 진행 중인 산책 세션을 연결, 없으면 null)
    public static WalkRequest create(Pet requesterDog, Pet receiverDog, String message, WalkSession walkSession) {
        return WalkRequest.builder()
                .requesterDog(requesterDog)
                .receiverDog(receiverDog)
                .message(message)
                .walkSession(walkSession)
                .build();
    }

    // 받은 요청 수락 (PENDING → ACCEPTED)
    public void accept() {
        changeStatus(WalkRequestStatus.ACCEPTED);
    }

    // 받은 요청 거절 (PENDING → REJECTED)
    public void reject() {
        changeStatus(WalkRequestStatus.REJECTED);
    }

    // 보낸 요청 취소 (PENDING → CANCELED)
    public void cancel() {
        changeStatus(WalkRequestStatus.CANCELED);
    }

    private void changeStatus(WalkRequestStatus next) {
        if (this.status != WalkRequestStatus.PENDING) {
            throw new GeneralException(ErrorStatus.WALK_REQUEST_NOT_PENDING);
        }
        this.status = next;
        this.respondedAt = LocalDateTime.now();
    }
}
