package com.pawmesh.backend.domain.auth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pawmesh.backend.common.properties.OpenAiProperties;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

// OpenAI Image API / Chat API 호출 클라이언트. (실연동 — 키 없으면 호출 시 예외)
@Component
public class OpenAiClient {

    private static final String BASE_URL = "https://api.openai.com/v1";

    // 강아지 사진을 캐릭터 아바타로 변환하는 프롬프트
    private static final String AVATAR_PROMPT =
            "Transform this dog into a cute, friendly 3D character avatar illustration. "
                    + "Keep the dog's breed, fur color, and distinctive features clearly recognizable. "
                    + "Use a clean solid pastel background, centered composition, app profile picture style.";

    // 견종/성격/소개글을 JSON 으로만 응답하도록 강제하는 시스템 프롬프트
    private static final String TEXT_SYSTEM_PROMPT =
            "You analyze a dog photo and respond with ONLY a JSON object, no extra text. "
                    + "Keys: \"breed\" (string, Korean), \"personalityTags\" (array of 3 to 5 short Korean strings), "
                    + "\"introText\" (string, a friendly one-line Korean introduction of the dog).";

    private final OpenAiProperties properties;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public OpenAiClient(OpenAiProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    // images/edits 로 아바타 이미지를 생성하고 디코드된 바이트(PNG)를 반환한다.
    public byte[] generateAvatar(byte[] originalImage, String contentType) {
        ensureApiKey();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", properties.imageModel());
        body.add("prompt", AVATAR_PROMPT);
        body.add("size", "1024x1024");
        body.add("quality", "medium");
        body.add("n", "1");
        body.add("image", imagePart(originalImage, contentType));

        String response = restClient.post()
                .uri("/images/edits")
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);

        String b64 = readJson(response).path("data").path(0).path("b64_json").asText(null);
        if (!StringUtils.hasText(b64)) {
            throw new IllegalStateException("OpenAI image response missing b64_json");
        }
        return Base64.getDecoder().decode(b64);
    }

    // chat/completions(비전 입력)으로 견종/성격/소개글을 생성한다.
    public PetProfileText generateProfileText(byte[] originalImage, String contentType, String petName) {
        ensureApiKey();

        String dataUrl = "data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(originalImage);
        Map<String, Object> requestBody = Map.of(
                "model", properties.chatModel(),
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", TEXT_SYSTEM_PROMPT),
                        Map.of("role", "user", "content", List.of(
                                Map.of("type", "text", "text", userPrompt(petName)),
                                Map.of("type", "image_url", "image_url", Map.of("url", dataUrl))
                        ))
                )
        );

        String response = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        String content = readJson(response).path("choices").path(0).path("message").path("content").asText(null);
        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("OpenAI chat response missing message content");
        }
        return parseProfileText(content);
    }

    // content(JSON 문자열)를 PetProfileText 로 파싱한다.
    private PetProfileText parseProfileText(String content) {
        JsonNode node = readJson(content);
        String breed = node.path("breed").asText("");
        String introText = node.path("introText").asText("");
        List<String> tags = new ArrayList<>();
        node.path("personalityTags").forEach(tag -> tags.add(tag.asText()));
        return new PetProfileText(breed, tags, introText);
    }

    private String userPrompt(String petName) {
        if (StringUtils.hasText(petName)) {
            return "이 강아지의 이름은 '" + petName + "'입니다. 사진을 보고 견종, 성격 태그 3~5개, 한 줄 소개글을 생성해주세요.";
        }
        return "사진을 보고 견종, 성격 태그 3~5개, 한 줄 소개글을 생성해주세요.";
    }

    // image part 에 파일명/콘텐츠 타입을 지정한다.
    private HttpEntity<ByteArrayResource> imagePart(byte[] image, String contentType) {
        ByteArrayResource resource = new ByteArrayResource(image) {
            @Override
            public String getFilename() {
                return "pet" + extensionOf(contentType);
            }
        };
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new HttpEntity<>(resource, headers);
    }

    private String extensionOf(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/webp" -> ".webp";
            default -> ".png";
        };
    }

    private JsonNode readJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse OpenAI response", e);
        }
    }

    private void ensureApiKey() {
        if (!StringUtils.hasText(properties.apiKey())) {
            throw new IllegalStateException("OPEN_AI_API_KEY is not configured");
        }
    }

    private String bearer() {
        return "Bearer " + properties.apiKey();
    }
}
