package com.pawmesh.backend.domain.user.enums;

// 산책 스타일(복수 선택). 화면 확정 시 항목이 추가/변경될 수 있다. (tasks.md 부록 B 참고)
public enum WalkStyle {
    LONG_WALK,      // 오래 걷는 산책
    SHORT_WALK,     // 짧은 산책
    ACTIVE_PLAY,    // 활발한 놀이
    RELAXED_WALK,   // 여유로운 산책
    SOCIAL_WALK,    // 다른 강아지와 어울리는 산책
    TRAINING        // 훈련 위주 산책
}
