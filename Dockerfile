# ===== 1) Build stage =====
# gradle wrapper(8.10) 를 그대로 사용해 버전 일치를 보장합니다.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# 의존성 캐시 레이어: 빌드 스크립트/래퍼 먼저 복사
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Windows(CRLF)에서 체크아웃된 gradlew 도 컨테이너에서 동작하도록 개행 정리 + 실행권한
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# 의존성 미리 받아 레이어 캐싱 (소스 변경 시 재다운로드 방지)
RUN ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

# 소스 복사 후 실행 가능한 bootJar 빌드 (테스트는 빌드 단계에서 제외)
COPY src src
RUN ./gradlew --no-daemon clean bootJar -x test

# ===== 2) Runtime stage =====
FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

# 비루트 사용자로 실행
RUN groupadd -r spring && useradd -r -g spring spring

# bootJar 결과물만 복사 (plain jar 는 생성되지 않음)
COPY --from=build /app/build/libs/*.jar app.jar
RUN chown spring:spring app.jar
USER spring

EXPOSE 8080

# 컨테이너 메모리에 맞춰 힙을 자동 조정 + 외부에서 JAVA_OPTS 주입 가능
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
