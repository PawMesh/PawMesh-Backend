package com.pawmesh.backend.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.openai")
public record OpenAiProperties(
        String apiKey,
        String imageModel,
        String chatModel
) {
}
