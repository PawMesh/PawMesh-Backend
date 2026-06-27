package com.pawmesh.backend.domain.map.enums;

/**
 * 산책 세션(walk_sessions.status)의 상태값.
 *
 * <p>DB의 status 컬럼은 ENUM('WALKING','MATCHED','ENDED') 이며,
 * 이 enum 의 이름(name)이 그대로 문자열로 저장/조회된다.</p>
 *
 * <ul>
 *   <li>{@link #WALKING} : 산책 시작 직후 기본 상태. 지도에 마커로 노출되고 nearby 조회 대상.</li>
 *   <li>{@link #MATCHED} : 다른 강아지와 매칭되어 함께 산책 중. partner-location 조회 가능.</li>
 *   <li>{@link #ENDED}   : 산책 종료(complete 또는 delete). 지도/ nearby 에서 제외.</li>
 * </ul>
 */
public enum WalkSessionStatus {

    /** 산책 중 (기본값). 지도에 노출되고 주변 조회 대상이 된다. */
    WALKING,

    /** 매칭됨. 산책 친구와 연결되어 partner-location 조회가 가능하다. */
    MATCHED,

    /** 종료됨. 지도와 주변 조회에서 제외된다. */
    ENDED
}
