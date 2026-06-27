package com.pawmesh.backend.domain.map.controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * map 도메인 컨트롤러 7개 엔드포인트가 Swagger(OpenAPI) 문서에 정상 노출되는지 검증.
 *
 * <p>nodb 프로필로 띄워 DB 없이도 컨텍스트가 부팅되는지 함께 검증한다.</p>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("nodb")
class MapSessionControllerSwaggerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("/v3/api-docs 에 map-sessions 7개 엔드포인트가 모두 노출된다")
    void mapSessionEndpointsAreExposedInOpenApiDocs() {
        ResponseEntity<JsonNode> response = restTemplate.getForEntity("/v3/api-docs", JsonNode.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        JsonNode paths = response.getBody().get("paths");
        assertThat(paths).as("paths 노드 존재").isNotNull();

        // 1. 산책 시작 (POST), 2. 지도 삭제 (DELETE) — 같은 경로 /v1/map-sessions
        assertThat(paths.has("/v1/map-sessions")).as("POST/DELETE /v1/map-sessions").isTrue();
        assertThat(paths.get("/v1/map-sessions").has("post")).as("산책 시작 POST").isTrue();

        // 3. 위치 업데이트 (PATCH)
        assertThat(paths.has("/v1/map-sessions/{sessionId}/location")).as("위치 업데이트").isTrue();
        assertThat(paths.get("/v1/map-sessions/{sessionId}/location").has("patch")).isTrue();

        // 4. 주변 강아지 조회 (GET)
        assertThat(paths.has("/v1/map-sessions/nearby")).as("주변 조회").isTrue();
        assertThat(paths.get("/v1/map-sessions/nearby").has("get")).isTrue();

        // 5. 강아지 카드 조회 (GET)
        assertThat(paths.has("/v1/map-sessions/{sessionId}/dog-card")).as("강아지 카드").isTrue();
        assertThat(paths.get("/v1/map-sessions/{sessionId}/dog-card").has("get")).isTrue();

        // 6. 산책 친구 위치 조회 (GET)
        assertThat(paths.has("/v1/map-sessions/{sessionId}/partner-location")).as("파트너 위치").isTrue();
        assertThat(paths.get("/v1/map-sessions/{sessionId}/partner-location").has("get")).isTrue();

        // 7. 산책 완료 (PATCH)
        assertThat(paths.has("/v1/map-sessions/{sessionId}/complete")).as("산책 완료").isTrue();
        assertThat(paths.get("/v1/map-sessions/{sessionId}/complete").has("patch")).isTrue();

        // 지도 삭제 (DELETE) — /v1/map-sessions/{sessionId}
        assertThat(paths.has("/v1/map-sessions/{sessionId}")).as("지도 삭제 경로").isTrue();
        assertThat(paths.get("/v1/map-sessions/{sessionId}").has("delete")).as("지도 삭제 DELETE").isTrue();

        // map-sessions 관련 오퍼레이션 총 7개 카운트
        long mapOperationCount = countMapSessionOperations(paths);
        assertThat(mapOperationCount).as("map-sessions 엔드포인트(메서드) 총 개수").isEqualTo(7);
    }

    private long countMapSessionOperations(JsonNode paths) {
        long count = 0;
        var pathNames = paths.fieldNames();
        while (pathNames.hasNext()) {
            String path = pathNames.next();
            if (!path.startsWith("/v1/map-sessions")) {
                continue;
            }
            JsonNode methods = paths.get(path);
            var methodNames = methods.fieldNames();
            while (methodNames.hasNext()) {
                methodNames.next();
                count++;
            }
        }
        return count;
    }
}
