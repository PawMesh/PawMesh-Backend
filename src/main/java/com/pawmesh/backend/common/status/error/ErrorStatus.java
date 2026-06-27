package com.pawmesh.backend.common.status.error;

import com.pawmesh.backend.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    /**
     * Common
     */
    BAD_REQUEST("COMM_400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("COMM_401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN("COMM_403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND("COMM_404", HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("COMM_405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR("COMM_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    /**
     * Auth
     */
    NICKNAME_DUPLICATED("AUTH_409", HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    SIGNUP_TOKEN_REQUIRED("AUTH_401", HttpStatus.UNAUTHORIZED, "회원가입 토큰이 필요합니다."),
    SIGNUP_SESSION_EXPIRED("AUTH_410", HttpStatus.GONE, "회원가입 세션이 만료되었습니다. 처음부터 다시 진행해주세요."),
    AI_JOB_NOT_FOUND("AUTH_404", HttpStatus.NOT_FOUND, "AI 생성 작업을 찾을 수 없습니다."),
    AI_PHOTO_GENERATION_FAILED("AUTH_502", HttpStatus.BAD_GATEWAY, "AI 프로필 생성에 실패했습니다."),
    INVALID_PET_IMAGE("AUTH_400", HttpStatus.BAD_REQUEST, "유효하지 않은 강아지 사진입니다."),
    LOGIN_FAILED("AUTH_401", HttpStatus.UNAUTHORIZED, "닉네임 또는 비밀번호가 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN("AUTH_401", HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),

    /**
     * User
     */
    USER_NOT_FOUND("USER_404", HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

    /**
     * Map (Walk Session)
     */
    WALK_SESSION_NOT_FOUND("WALK_404", HttpStatus.NOT_FOUND, "산책 세션을 찾을 수 없습니다."),
    WALK_SESSION_ALREADY_ENDED("WALK_400", HttpStatus.BAD_REQUEST, "이미 종료된 산책 세션입니다."),
    INVALID_ROUTE_PATH("WALK_400", HttpStatus.BAD_REQUEST, "경로(routePath) 변환에 실패했습니다."),
    PARTNER_NOT_MATCHED("WALK_404", HttpStatus.NOT_FOUND, "매칭된 산책 친구가 없습니다."),

    /**
     * Friendship
     */
    FRIENDSHIP_NOT_FOUND("FRIEND_404", HttpStatus.NOT_FOUND, "친구 신청을 찾을 수 없습니다."),
    FRIENDSHIP_ALREADY_EXISTS("FRIEND_409", HttpStatus.CONFLICT, "이미 친구 신청이 존재합니다."),
    FRIENDSHIP_NOT_PENDING("FRIEND_409", HttpStatus.CONFLICT, "이미 처리된 친구 신청입니다."),
    FRIEND_DOG_NOT_FOUND("FRIEND_404", HttpStatus.NOT_FOUND, "친구 강아지를 찾을 수 없습니다."),

    /**
     * Walk Request
     */
    WALK_REQUEST_NOT_FOUND("WALK_404", HttpStatus.NOT_FOUND, "산책 요청을 찾을 수 없습니다."),
    WALK_REQUEST_ALREADY_EXISTS("WALK_409", HttpStatus.CONFLICT, "이미 대기중인 산책 요청이 있습니다."),
    WALK_REQUEST_NOT_PENDING("WALK_409", HttpStatus.CONFLICT, "이미 처리된 산책 요청입니다."),

    /**
     * Dog
     */
    PET_NOT_FOUND("DOG_404", HttpStatus.NOT_FOUND, "강아지를 찾을 수 없습니다."),
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
