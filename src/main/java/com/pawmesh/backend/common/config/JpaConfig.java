package com.pawmesh.backend.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// BaseEntity 의 @CreatedDate / @LastModifiedDate 자동 채움을 활성화한다.
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
