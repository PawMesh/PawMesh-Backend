package com.pawmesh.backend.common.config;

import com.pawmesh.backend.common.properties.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    // 자격증명은 환경변수(AWS_ACCESS_KEY_ID/AWS_SECRET_ACCESS_KEY) 또는 IAM Role 에서 해석된다.
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Properties.region()))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
