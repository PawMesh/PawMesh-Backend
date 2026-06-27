package com.pawmesh.backend.domain.walk.repository;

import com.pawmesh.backend.domain.walk.entity.WalkSession;
import org.springframework.data.jpa.repository.JpaRepository;

// 산책 세션(WALK_SESSIONS) 저장소. JpaRepository 상속으로 기본 CRUD 자동 제공.
public interface WalkSessionRepository extends JpaRepository<WalkSession, Long> {
}
