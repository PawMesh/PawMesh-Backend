package com.pawmesh.backend.common.apiPayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * 모든 API 응답을 감싸는 공통 응답 봉투.
 *
 * <p>프론트(안드로이드)와 약속한 고정 형식:</p>
 * <pre>{@code
 * {
 *   "isSuccess": true,
 *   "code": "WALK_200",
 *   "message": "요청에 성공했습니다.",
 *   "data": { ... }
 * }
 * }</pre>
 *
 * <p>비유: 택배 상자(이 봉투) 안에 실제 물건(data)을 넣어 보낸다.
 * 성공/실패 여부(isSuccess), 송장 코드(code), 안내문(message)이 상자 겉면에 적혀 있다.</p>
 *
 * @param <T> data 에 담기는 실제 응답 본문 타입
 */
@JsonInclude(JsonInclude.Include.ALWAYS)
@JsonPropertyOrder({"isSuccess", "code", "message", "data"})
public record ApiResponse<T>(
        @JsonProperty("isSuccess") boolean isSuccess,
        String code,
        String message,
        T data
) {

    /** 성공 응답 - 코드/메시지를 직접 지정. (예: 산책 시작 시 WALK_201) */
    public static <T> ApiResponse<T> onSuccess(String code, String message, T data) {
        return new ApiResponse<>(true, code, message, data);
    }

    /** 성공 응답 - 기본 코드/메시지(COMMON_200). */
    public static <T> ApiResponse<T> onSuccess(T data) {
        return new ApiResponse<>(true, "COMMON_200", "요청에 성공했습니다.", data);
    }

    /** 실패 응답 - 코드/메시지를 직접 지정. (전역 예외 처리에서 사용 예정) */
    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}
