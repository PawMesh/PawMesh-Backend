package com.pawmesh.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.signup")
public record SignupProperties(
        long sessionTtlMinutes
) {
}
