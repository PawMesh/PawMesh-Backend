# 프로젝트 코드 컨벤션

> 이 문서는 백엔드(Spring Boot) 코드 작성 시 따라야 할 규칙을 정의한다.
> 코드를 생성/수정할 때는 항상 이 컨벤션을 우선한다.

## HTTP Method
- `GET` : 데이터 조회
- `POST` : 데이터 생성
- `PATCH` : 데이터 일부 수정
- `DELETE` : 데이터 삭제

---

## 1. 프로젝트 구조

```
src
 └ main
    └ common
       └ exception
       └ response
       └ status
          └ error
          └ success
       └ base
       └ config
       └ jwt
       └ properties
    └ domain
       └ user
          └ entity
          └ controller
          └ service
          └ repository
          └ enums
          └ dto
             └ request
             └ response
```

- **Service는 인터페이스/구현체 분리 없이 단일 클래스로 작성한다.**
- **CQRS 패턴**을 적용한다. (예: `UserCommandService`, `UserQueryService`)

### 공통 Status

```java
public interface BaseStatus {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
```

```java
public enum SuccessStatus implements BaseStatus {

    SUCCESS_200("ROOME_200", HttpStatus.OK, "성공입니다."),
    SUCCESS_201("ROOME_201", HttpStatus.CREATED, "성공입니다."),
    SUCCESS_204("ROOME_204", HttpStatus.NO_CONTENT, "성공입니다."),

    /**
     * Auth
     */
    AUTH_URL_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 URL 조회 성공"),
    LOGIN_SUCCESS("AUTH_200", HttpStatus.OK, "로그인 성공"),
    LOGOUT_SUCCESS("AUTH_200", HttpStatus.OK, "로그아웃 성공"),
    CREATE_USER_SUCCESS("AUTH_201", HttpStatus.CREATED, "회원가입 성공"),
    DELETE_USER_SUCCESS("AUTH_200", HttpStatus.OK, "회원탈퇴 성공"),
    CHECK_ID_SUCCESS("AUTH_200", HttpStatus.OK, "아이디 중복 확인 성공"),
    CHECK_NICKNAME_SUCCESS("AUTH_200", HttpStatus.OK, "닉네임 중복 확인 성공"),
    UPDATE_PASSWORD_SUCCESS("AUTH_200", HttpStatus.OK, "비밀번호 변경 성공"),
    FIND_EMAIL_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 찾기 성공"),
    CHECK_EMAIL_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 중복 확인 성공"),
    SEND_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 코드 발송 성공"),
    CONFIRM_EMAIL_VERIFICATION_SUCCESS("AUTH_200", HttpStatus.OK, "이메일 인증 성공"),
    CREATE_TOKEN_SUCCESS("AUTH_200", HttpStatus.OK, "토큰 재발급 성공"),
    CONFIRM_PASSWORD_SUCCESS("AUTH_200", HttpStatus.OK, "비밀번호 검증 성공"),
    ;
}
```

```java
@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseStatus {

    // 예시
    ERROR_STATUS("ROOME_400", HttpStatus.BAD_REQUEST, "Bad Request"),

    /**
     * Common
     */
    BAD_REQUEST("COMM_400", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED("COMM_401", HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN("COMM_403", HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND("COMM_404", HttpStatus.NOT_FOUND, "요청한 자원을 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED("COMM_405", HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 메소드입니다."),
    INTERNAL_SERVER_ERROR("COMM_500", HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    ;
}
```

---

## 2. 네이밍 컨벤션

- **클래스명**: `PascalCase` (예: `UserCommandService`, `UserQueryService`, `OrderController`)
- **메서드명**: `camelCase` (예: `getUserById()`, `processOrder()`)
  - Controller → `동사(Method) + 명사(주체)`
  - Service → `동사(Method) + 명사(목적 대상)`
  - 생성: `create`, `register`(회원가입)
  - 조회: `get[도메인명][세부정보]`
  - 수정: `update`
  - 삭제: `delete`
- **변수명**: `camelCase` (예: `userList`, `orderId`)
  - 명확하게 표시: `orderId`, `userId`
  - `is~~` 사용 X → `~~Status`
  - 복수형은 `변수명s`
- **상수**: `UPPER_SNAKE_CASE` (예: `MAX_USER_LIMIT`)
- **패키지명**: 소문자, 점(`.`)으로 구분 (예: `com.example.project.service`)
- **엔티티 클래스**: 단수형 (예: `User`, `Order`)
- **테이블명**: 복수형 (예: `users`, `orders`)
- **API 엔드포인트**: `kebab-case` (예: `/users`, `/orders`)
  - 예외적으로 `/auth`
  - 예: `api/users/profile`

---

## 3. 코드 스타일

- **중괄호 `{}` 는 항상 사용한다.** (단일 문장이어도 생략 금지)

### Lombok
- DTO 및 단순 모델 클래스에서만 제한적으로 사용 (`@Getter`, `@NoArgsConstructor` 등)
- **`@Setter`는 사용하지 않는다 → Builder로 대체**
- DTO는 `record` 클래스로 구성

```java
public record ChatProductScenarioResponse(
        ChatMode chatMode,
        List<ProductSummaryResponse> products
) {
    public static ChatProductScenarioResponse from(List<ProductSummaryResponse> productSummaryResponseList) {
        return new ChatProductScenarioResponse(ChatMode.PRODUCT, productSummaryResponseList);
    }
}
```

### Optional
- 리턴 타입에 `Optional<T>` 사용 후, null이면 Exception 처리

### 계층 참조 규칙
- **Controller → Service 하나만 참조**
- **Service → Repository 하나만 참조 + Converter 하나 참조**, `/** **/` 주석 필수
  - 한 서비스에서 공통으로 쓰는 로직이 아니면 private 메서드 사용 X
- **Repository → JPA, QueryDSL 사용** (복잡한 쿼리는 QueryDSL)
- **DTO → 한 Java 파일에 하나의 DTO만 선언** (예: `UpdateProfileRequest`)

---

## 4. Spring 설정

- `@RestController` : REST API 컨트롤러
- `@Service` : 비즈니스 로직 클래스
- `@Repository` : 데이터 액세스 계층
- `@Transactional` : 서비스 계층 트랜잭션 관리
  - **클래스 최상단에서 `@Transactional` 선언 후, 각 메서드에서 알맞게 추가 변경**

```java
@Transactional
public User signUp(SignUpRequest signUpRequest) {
    log.info("SignUp request started for userName: {}", signUpRequest.getUserName());
    // check email, nickname duplicate
    checkForDuplicates(signUpRequest);
    // PasswordEncoding
    String encodedPassword = encodePassword(signUpRequest.getPassword());
    // save user
    User user = saveUser(signUpRequest, encodedPassword);

    log.info("SignUp completed for userName: {} with userId: {}", user.getUserName(), user.getUserId());
    return user;
}
```

- **CUD 작업은 반환값으로 `~Id` 하나만 반환**

```json
{
  "code": 200,
  "timestamp": "",
  "message": "",
  "result": {
    "~Id": 1
  }
}
```

- `@Valid` : 요청 DTO 검증 (필요 시 커스텀 가능)

---

## 5. API 설계 원칙

- RESTful API 원칙 준수
- HTTP 메서드 사용 기준
  - `GET` : 데이터 조회
  - `POST` : 데이터 생성
  - `PUT` : 데이터 전체 업데이트 (**왠만하면 사용하지 않음 → PATCH 사용**)
  - `PATCH` : 데이터 일부 업데이트
  - `DELETE` : 데이터 삭제
- 응답 규격
  - 정상 응답: `{ "status": "success", "data": ... }`
  - 에러 응답: `{ "status": "error", "message": "에러 메시지" }`

### 공통 응답/예외 클래스

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "data"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private boolean isSuccess;
    private String code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static ResponseEntity<ApiResponse<Void>> success(BaseStatus successStatus) {
        return ResponseEntity
                .status(successStatus.getHttpStatus())
                .body(new ApiResponse<>(true, successStatus.getCode(), successStatus.getMessage(), null));
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(BaseStatus successStatus, T data) {
        return ResponseEntity
                .status(successStatus.getHttpStatus())
                .body(new ApiResponse<>(true, successStatus.getCode(), successStatus.getMessage(), data));
    }

    public static ResponseEntity<ApiResponse<Void>> error(BaseStatus errorStatus) {
        return ResponseEntity
                .status(errorStatus.getHttpStatus())
                .body(new ApiResponse<>(false, errorStatus.getCode(), errorStatus.getMessage(), null));
    }

    public static ResponseEntity<ApiResponse<Void>> error(BaseStatus errorStatus, String message) {
        return ResponseEntity
                .status(errorStatus.getHttpStatus())
                .body(new ApiResponse<>(false, errorStatus.getCode(), message, null));
    }
}
```

```java
@Getter
public class GeneralException extends RuntimeException {
    private final BaseStatus errorStatus;

    public GeneralException(BaseStatus errorStatus) {
        super(errorStatus.getMessage());
        this.errorStatus = errorStatus;
    }
}
```

```java
@RestControllerAdvice
@Slf4j
public class GeneralExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(GeneralException e) {
        if (e.getErrorStatus().getHttpStatus().is5xxServerError()) {
            log.error("[*] GeneralException :", e);
        } else {
            log.warn("[*] GeneralException : {}", e.getMessage());
        }
        return ApiResponse.error(e.getErrorStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        String errorMessage = "잘못된 요청입니다: " + e.getMessage();
        log.error("[*] IllegalArgumentException :", e);
        return ApiResponse.error(ErrorStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponse<Void>> handleNullPointerException(NullPointerException e) {
        String errorMessage = "서버에서 예기치 않은 오류가 발생했습니다. 요청을 처리하는 중에 Null 값이 참조되었습니다.";
        log.error("[*] NullPointerException :", e);
        return ApiResponse.error(ErrorStatus.INTERNAL_SERVER_ERROR, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[*] Internal Server Error :", e);
        return ApiResponse.error(ErrorStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        BaseStatus errorCode = ErrorStatus.BAD_REQUEST;
        String errorMessage = ex.getBindingResult().getFieldErrors().isEmpty()
                ? errorCode.getMessage()
                : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        ApiResponse<Void> body = createApiResponse(errorCode, errorMessage);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        BaseStatus errorCode = ErrorStatus.METHOD_NOT_ALLOWED;
        ApiResponse<Void> body = createApiResponse(errorCode, null);
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    private ApiResponse<Void> createApiResponse(BaseStatus errorStatus, String errorMessage) {
        return new ApiResponse<>(
                false,
                errorStatus.getCode(),
                (errorMessage != null ? errorMessage : errorStatus.getMessage()),
                null
        );
    }
}
```

---

## 6. 예외 처리

- 공통 예외 핸들러 작성 (`@RestControllerAdvice` 활용)
- 예외 클래스 정의 (`GeneralException`, `CustomException`, `NotFoundException` 등)
- 적절한 HTTP 상태 코드 반환 (`400 BAD REQUEST`, `404 NOT FOUND`, `500 INTERNAL SERVER ERROR`)

```java
switch (type) {
    case LOGIN, OTHER -> {
        if (!isMatch) throw new GeneralException(ErrorStatus.INVALID_PASSWORD);
    }
    case UPDATE -> {
        if (isMatch) throw new GeneralException(ErrorStatus.PASSWORD_SAME_AS_OLD);
    }
    default -> throw new IllegalStateException("Unexpected value: " + type);
}
```

---

## 7. 데이터베이스 설정

- JPA 사용 시 `@Entity`, `@Table` 지정 필수
- 명확한 연관관계 설정 (`@OneToMany`, `@ManyToOne` 등)
- `@Indexed` 어노테이션으로 검색 최적화 (개발 완료 후 적용 예정)
- 엔티티 어노테이션
  - `@Entity`
  - `@Getter`
  - `@NoArgsConstructor(access = AccessLevel.PROTECTED)`
  - `@AllArgsConstructor`
  - `@Builder`
  - ❌ **사용 금지** : `@Setter`

---

## 8. 로깅 및 모니터링

- `SLF4J` + `Logback` 사용
- `@Slf4j` 어노테이션 활용
- `Spring Boot Actuator`로 시스템 모니터링 (`/actuator/health`, `/actuator/metrics`) — 설정 권장
- `Discord Webhook AOP` 설정

---

## 9. 캐싱 및 성능 최적화

- Lazy Loading 및 Batch Fetching 사용 고려 (`@OneToMany(fetch = FetchType.LAZY)`)

---

## 10. 테스트 / 문서화

- **Swagger** 적용

---

## 11. 코드 자동 정렬 (IntelliJ IDEA)

- Windows: `Ctrl + Alt + L`
- Mac: `⌘ + ⌥ + L`

---

## 12. 주석

- 서비스 등 코드에서
  - 함수 최상단에 **무슨 함수인지** 한 줄
  - 함수가 너무 길면 **세부 로직을 간단히** 주석
