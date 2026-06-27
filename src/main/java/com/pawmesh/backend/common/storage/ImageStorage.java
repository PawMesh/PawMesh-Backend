package com.pawmesh.backend.common.storage;

import org.springframework.web.multipart.MultipartFile;

// 이미지 저장소 추상화. 구현은 S3ImageStorage (추후 로컬 구현으로 교체 가능).
public interface ImageStorage {

    // MultipartFile 을 dir 하위에 업로드하고 저장 key 를 반환한다.
    String upload(MultipartFile file, String dir);

    // 바이트 배열(예: AI 생성 이미지)을 dir 하위에 업로드하고 저장 key 를 반환한다.
    String upload(byte[] bytes, String contentType, String dir);

    // 저장 key 의 원본 바이트를 내려받는다. (예: AI 생성 시 원본 사진 재사용)
    byte[] download(String key);

    // 저장 key 로 접근 가능한 URL 을 반환한다.
    String getUrl(String key);
}
