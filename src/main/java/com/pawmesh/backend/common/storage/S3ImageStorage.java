package com.pawmesh.backend.common.storage;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.properties.S3Properties;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageStorage implements ImageStorage {

    private static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    // MultipartFile 을 S3 에 업로드한다.
    @Override
    public String upload(MultipartFile file, String dir) {
        try {
            return upload(file.getBytes(), file.getContentType(), dir);
        } catch (IOException e) {
            log.warn("[*] S3 upload failed while reading multipart file : {}", e.getMessage());
            throw new GeneralException(ErrorStatus.INVALID_PET_IMAGE);
        }
    }

    // 바이트 배열을 S3 에 업로드한다.
    @Override
    public String upload(byte[] bytes, String contentType, String dir) {
        String resolvedContentType = (contentType != null) ? contentType : DEFAULT_CONTENT_TYPE;
        String key = buildKey(dir, resolvedContentType);

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .contentType(resolvedContentType)
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(bytes));
        return key;
    }

    // 저장 key 의 바이트를 내려받는다.
    @Override
    public byte[] download(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(s3Properties.bucket())
                .key(key)
                .build();
        return s3Client.getObjectAsBytes(request).asByteArray();
    }

    // virtual-hosted style URL 을 생성한다.
    @Override
    public String getUrl(String key) {
        return "https://" + s3Properties.bucket() + ".s3." + s3Properties.region() + ".amazonaws.com/" + key;
    }

    // dir/{uuid}.{ext} 형태의 저장 key 를 만든다.
    private String buildKey(String dir, String contentType) {
        String extension = resolveExtension(contentType);
        String fileName = UUID.randomUUID() + extension;
        if (StringUtils.hasText(dir)) {
            return dir + "/" + fileName;
        }
        return fileName;
    }

    // content-type 으로 확장자를 추정한다.
    private String resolveExtension(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> "";
        };
    }
}
