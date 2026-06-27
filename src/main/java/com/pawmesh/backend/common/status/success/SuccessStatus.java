package com.pawmesh.backend.common.status.success;

import com.pawmesh.backend.common.status.BaseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseStatus {

    /**
     * Common
     */
    SUCCESS_200("COMM_200", HttpStatus.OK, "성공입니다."),
    SUCCESS_201("COMM_201", HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204("COMM_204", HttpStatus.NO_CONTENT, "성공입니다."),

    /**
     * Auth
     */
    CHECK_NICKNAME_SUCCESS("AUTH_200", HttpStatus.OK, "닉네임 중복 확인 성공"),
    UPLOAD_PET_IMAGE_SUCCESS("AUTH_201", HttpStatus.CREATED, "강아지 사진 업로드 성공"),
    AI_PHOTO_JOB_ACCEPTED("AUTH_202", HttpStatus.ACCEPTED, "AI 프로필 생성 요청 접수"),
    AI_PHOTO_JOB_SUCCESS("AUTH_200", HttpStatus.OK, "AI 프로필 생성 상태 조회 성공"),
    CREATE_USER_SUCCESS("AUTH_201", HttpStatus.CREATED, "회원가입 성공"),
    LOGIN_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 성공"),
    REISSUE_TOKEN_SUCCESS("AUTH_200", HttpStatus.OK, "토큰 재발급 성공"),

    /**
     * User
     */
    GET_HUMAN_PROFILE_SUCCESS("USER_200", HttpStatus.OK, "보호자 프로필 조회 성공"),
    UPDATE_HUMAN_PROFILE_SUCCESS("USER_200", HttpStatus.OK, "보호자 프로필 수정 성공"),
    GET_DOG_PROFILE_SUCCESS("USER_200", HttpStatus.OK, "강아지 프로필 조회 성공"),
    UPDATE_DOG_PROFILE_SUCCESS("USER_200", HttpStatus.OK, "강아지 프로필 수정 성공"),
    DELETE_USER_SUCCESS("USER_200", HttpStatus.OK, "계정 삭제 성공"),
    ;

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;
}
