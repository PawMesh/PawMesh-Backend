# PawMesh — 회원가입 / AI 펫 프로필 백엔드 작업 지시서

> **전제**
> - 모든 코드 컨벤션은 프로젝트 루트의 `CLAUDE.md`를 따른다. (CQRS, record DTO, `ApiResponse<T>`, `SuccessStatus`/`ErrorStatus`, `GeneralException`, `@Transactional`는 클래스 최상단 선언, CUD는 `~Id` 하나만 반환 등)
> - 이 문서는 Task 단위로 끊어서 진행한다. **Task 0 → 1 → 2 → 3 → 4 → 5 순서로** 의존성이 있으니 위에서부터 진행할 것.
> - 한 Task가 끝나면 컴파일/기동이 되는 상태로 만든 뒤 다음 Task로 넘어간다.

---

## 0. 핵심 설계 (모든 Task 공통 맥락)

### 회원가입 플로우 (signup 토큰 방식)

닉네임·비밀번호는 **마지막 단계**에 들어온다. 그 전 단계(사진 업로드 → AI 생성 → 폴링)를 하나로 묶기 위해 **짧은 수명의 signup 토큰**을 쓴다. 정식 `User`는 마지막 `complete`에서 단 한 번 생성한다. (미완성 User를 DB에 만들지 않는다.)

```
[1] 사진 업로드   POST /v1/auth/signup/pet-images
        → 서버: 원본 이미지 저장, signupToken 발급(Redis 세션, TTL 30분), 반환
[2] AI 생성 시작  POST /v1/auth/signup/pet-images/ai-photos   (헤더: X-Signup-Token)
        → 서버: jobId 발급 후 OpenAI 호출을 비동기 실행, 즉시 202 반환
[3] 폴링         GET  /v1/auth/signup/pet-images/ai-photos/{jobId}  (헤더: X-Signup-Token)
        → status(PENDING/PROCESSING/DONE/FAILED) + DONE이면 결과 반환
[4] 회원가입 완료 POST /v1/auth/signup/complete  (헤더: X-Signup-Token)
        → 서버: 토큰 검증 → User + Pet 동시 생성 → 세션 폐기 → userId 반환
[5] 로그인        POST /v1/auth/login  (nickname + password → JWT)
```

- **signupToken**: 불투명 토큰(UUID). Redis 키 `signup:{token}` 에 세션 JSON 저장, **TTL 30분**.
  세션에는 업로드 원본 이미지 key, 진행 중인 jobId, AI 생성 결과를 누적 저장한다.
- 전달 헤더명: `X-Signup-Token` (Authorization과 구분 — 아직 정식 인증 전 단계이므로).
- TTL이 만료되면 `SIGNUP_SESSION_EXPIRED` 에러. 이탈한 세션/이미지는 TTL로 자동 정리.

### 기술 스택 / 인프라 전제
- Spring Boot, Spring Security(JWT), Spring Data JPA + QueryDSL, **Redis**(signup 세션 + AI job 상태).
- 이미지 저장소: `ImageStorage` 추상화로 두고 구현은 S3(또는 로컬). 메서드: `String upload(MultipartFile/byte[], dir)`, `String getUrl(key)`.
- 외부 AI: **OpenAI Image API** (아래 Task 3 상세).
- 비동기: `@Async` + 전용 `Executor` 또는 간단한 작업 큐. (MVP는 `@Async`로 충분)

### Redis 키 규칙
| 용도 | 키 | 값 | TTL |
|---|---|---|---|
| signup 세션 | `signup:{signupToken}` | 세션 JSON(이미지key, jobId, 생성결과) | 30분 |
| AI job 상태 | `ai-job:{jobId}` | 상태 JSON(status, result, error) | 30분 |

---

## Task 0 — 공통 인프라

**목표**: 컨벤션 문서의 공통 응답/예외 인프라를 코드로 옮긴다.

생성할 파일 (컨벤션 문서에 코드 원본이 있으니 그대로 사용):
- `common/response/ApiResponse<T>`
- `common/status/BaseStatus` (인터페이스)
- `common/status/success/SuccessStatus` (enum)
- `common/status/error/ErrorStatus` (enum)
- `common/exception/GeneralException`
- `common/exception/GeneralExceptionAdvice` (`@RestControllerAdvice`)

추가로 이 프로젝트에서 쓸 Status를 미리 enum에 정의해 둔다 (다음 Task들에서 참조):

```
// SuccessStatus
CHECK_NICKNAME_SUCCESS   ("AUTH_200", OK,        "닉네임 중복 확인 성공")
UPLOAD_PET_IMAGE_SUCCESS ("AUTH_201", CREATED,   "강아지 사진 업로드 성공")
AI_PHOTO_JOB_ACCEPTED    ("AUTH_202", ACCEPTED,  "AI 프로필 생성 요청 접수")
AI_PHOTO_JOB_SUCCESS     ("AUTH_200", OK,        "AI 프로필 생성 상태 조회 성공")
CREATE_USER_SUCCESS      ("AUTH_201", CREATED,   "회원가입 성공")
LOGIN_SUCCESS            ("AUTH_200", OK,        "로그인 성공")
GET_HUMAN_PROFILE_SUCCESS("USER_200", OK,        "보호자 프로필 조회 성공")
UPDATE_HUMAN_PROFILE_SUCCESS("USER_200", OK,     "보호자 프로필 수정 성공")
GET_DOG_PROFILE_SUCCESS  ("USER_200", OK,        "강아지 프로필 조회 성공")
UPDATE_DOG_PROFILE_SUCCESS("USER_200", OK,       "강아지 프로필 수정 성공")
DELETE_USER_SUCCESS      ("USER_200", OK,        "계정 삭제 성공")

// ErrorStatus
NICKNAME_DUPLICATED      ("AUTH_409", CONFLICT,     "이미 사용 중인 닉네임입니다.")
SIGNUP_TOKEN_REQUIRED    ("AUTH_401", UNAUTHORIZED, "회원가입 토큰이 필요합니다.")
SIGNUP_SESSION_EXPIRED   ("AUTH_410", GONE,         "회원가입 세션이 만료되었습니다. 처음부터 다시 진행해주세요.")
AI_JOB_NOT_FOUND         ("AUTH_404", NOT_FOUND,    "AI 생성 작업을 찾을 수 없습니다.")
AI_PHOTO_GENERATION_FAILED("AUTH_502", BAD_GATEWAY, "AI 프로필 생성에 실패했습니다.")
INVALID_PET_IMAGE        ("AUTH_400", BAD_REQUEST,  "유효하지 않은 강아지 사진입니다.")
LOGIN_FAILED             ("AUTH_401", UNAUTHORIZED, "닉네임 또는 비밀번호가 올바르지 않습니다.")
USER_NOT_FOUND           ("USER_404", NOT_FOUND,    "사용자를 찾을 수 없습니다.")
```

---

## Task 1 — 엔티티 & enum & Repository

**목표**: `User`(보호자), `Pet`(강아지) 엔티티와 enum, Repository를 만든다.

### enum (`domain/user/enums`)
- `Gender` { MALE, FEMALE }  — 보호자/강아지 공용
- `AgeGroup` { TEENS, TWENTIES, THIRTIES, FORTIES, FIFTIES_PLUS } — 보호자 연령대
- `WalkStyle` { LONG_WALK, ACTIVE_PLAY, SHORT_WALK, ... } — 산책 스타일(복수 선택)

### `User` (보호자)  테이블 `users`
- `userId` (PK, IDENTITY)
- `nickname` (unique, not null) — 로그인 ID
- `password` (not null, 인코딩 저장)
- `gender` (Gender)
- `ageGroup` (AgeGroup)
- `walkStyles` (List<WalkStyle>, `@ElementCollection`)
- 매칭 필터: `matchGender`(Gender, nullable=모두), `matchAgeFrom`, `matchAgeTo`
- 연관: `@OneToOne` 또는 `@OneToMany` Pet (MVP는 1보호자-1강아지 → OneToOne 권장)
- 어노테이션: `@Entity @Getter @NoArgsConstructor(access = PROTECTED) @AllArgsConstructor @Builder` (**`@Setter` 금지**)

### `Pet` (강아지)  테이블 `pets`
- `petId` (PK), `petName`(별명, not null)
- `breed`(견종), `personalityTags`(List<String>, `@ElementCollection`)
- `caution`(주의사항, TEXT), `introText`(AI 소개글, TEXT)
- `avatarImageUrl`(AI 생성 캐릭터 이미지 URL), `originalImageUrl`(원본 사진 URL)
- 연관: `@ManyToOne`/`@OneToOne` User

### Repository
- `UserRepository extends JpaRepository<User, Long>`
  - `boolean existsByNickname(String nickname)`
  - `Optional<User> findByNickname(String nickname)`
- `PetRepository extends JpaRepository<Pet, Long>`

> 연관관계/단복수 명명은 컨벤션 문서 7번 항목을 따른다. 복잡한 조회는 이후 QueryDSL로.

---

## Task 2 — 회원가입 세션 + 사진 업로드

**목표**: 강아지 원본 사진을 업로드받아 저장하고, signup 세션과 토큰을 발급한다. 닉네임 중복확인도 여기서 함께 만든다.

### 2-1. 닉네임 중복 확인
- `GET /v1/auth/check-nickname?nickname={nickname}`
- 응답: `data { available: boolean }`
- 사용중이면 `available=false` 반환 (여기선 에러로 던지지 않고 boolean으로). 최종 가입 시 한 번 더 검증.

### 2-2. 강아지 사진 업로드
- `POST /v1/auth/signup/pet-images`
- Request: `multipart/form-data`, 파트 `image` (PNG/JPG, 25MB 미만 — OpenAI edits 제약과 맞춤)
- 처리 (AuthCommandService):
  1. 이미지 형식/용량 검증 → 실패 시 `GeneralException(INVALID_PET_IMAGE)`
  2. `ImageStorage`로 원본 업로드 → `originalImageKey` 획득
  3. `signupToken = UUID`, Redis `signup:{token}` 에 세션 저장 `{ originalImageKey }`, TTL 30분
  4. 응답
- Response: `data { signupToken, originalImageUrl }`  (SuccessStatus.UPLOAD_PET_IMAGE_SUCCESS)

### 공통: signup 토큰 검증 컴포넌트
- 헤더 `X-Signup-Token`을 읽어 Redis 세션을 조회하는 `SignupSessionService` 작성.
  - 토큰 없음 → `SIGNUP_TOKEN_REQUIRED`
  - 세션 없음/만료 → `SIGNUP_SESSION_EXPIRED`
  - 정상 → 세션 객체 반환 / `update(token, session)` 으로 갱신(TTL 유지)
- Task 3, 4에서 재사용한다.

---

## Task 3 — AI 펫 프로필 생성 (OpenAI 연동, 비동기 + 폴링)

**목표**: 업로드된 강아지 사진으로 (1) 캐릭터 아바타 이미지, (2) 견종/성격태그/소개글 텍스트를 생성한다. 생성은 비동기로 돌리고 클라이언트는 폴링한다.

### OpenAI 호출 스펙 (확정)
- **아바타 이미지**: `POST https://api.openai.com/v1/images/edits`
  - `multipart/form-data` : `image` = 원본 강아지 사진, `prompt` = 캐릭터화 프롬프트,
    `model` = `gpt-image-1` (품질 ↑ 필요 시 `gpt-image-1.5`), `size` = `1024x1024`, `quality` = `medium`, `n` = `1`
  - **응답은 `data[0].b64_json` (base64)** — GPT Image 모델은 URL 반환 미지원. 서버가 디코드하여 `ImageStorage`에 저장 후 URL 생성.
  - 헤더: `Authorization: Bearer {OPENAI_API_KEY}`
  - ⚠️ GPT Image 모델은 **OpenAI 조직 인증 필요**. 평균 응답 ~30초 → 동기 처리 금지, 반드시 비동기.
- **견종/성격/소개글 텍스트**: `POST https://api.openai.com/v1/chat/completions`
  - 비전 입력(원본 이미지)으로 견종 추정 + 성격 태그 3~5개 + 한 줄 소개글 생성.
  - **JSON only로 응답하도록 시스템 프롬프트에 명시** → 파싱. 예: `{ "breed": "...", "personalityTags": [...], "introText": "..." }`
- `OPENAI_API_KEY`는 코드에 하드코딩 금지. `application.yml` / 환경변수 → `@ConfigurationProperties`로 주입.

### 3-1. AI 생성 시작
- `POST /v1/auth/signup/pet-images/ai-photos`  (헤더 `X-Signup-Token`)
- Request body(선택): `{ petName }` (별명; 소개글 프롬프트에 활용)
- 처리:
  1. signup 세션 검증 → 원본 이미지 key 확보
  2. `jobId = UUID`, Redis `ai-job:{jobId}` = `{ status: PROCESSING }`, 세션에 jobId 기록
  3. **`@Async`로 generateAsync(jobId, imageKey, petName) 실행** (OpenAI 이미지+텍스트 병렬 호출 권장)
     - 성공: 아바타 저장 → `ai-job:{jobId}` = `{ status: DONE, result: {...} }`
     - 실패: `{ status: FAILED, error }` (예외는 잡아서 상태에만 기록, 스레드 죽이지 않기)
  4. 즉시 `202 ACCEPTED` 반환
- Response: `data { jobId }`  (SuccessStatus.AI_PHOTO_JOB_ACCEPTED)

### 3-2. 생성 폴링
- `GET /v1/auth/signup/pet-images/ai-photos/{jobId}`  (헤더 `X-Signup-Token`)
- 처리: `ai-job:{jobId}` 조회 → 없으면 `AI_JOB_NOT_FOUND`
- Response:
  - 진행 중: `data { status: "PROCESSING" }`
  - 완료: `data { status: "DONE", avatarImageUrl, breed, personalityTags, introText }`
  - 실패: `data { status: "FAILED" }` (또는 `AI_PHOTO_GENERATION_FAILED` 에러로 내려도 됨 — 택1, 클라와 합의)
- DONE 시 결과를 signup 세션에도 병합 저장(complete에서 쓰기 위함). 클라가 결과를 수정(견종/소개글 편집)해서 complete로 보낼 수도 있게 한다.

> 클라이언트 폴링 간격은 2~3초 권장. 타임아웃(예: 60초) 넘으면 클라에서 재시도 안내.

---

## Task 4 — 회원가입 완료 + 로그인

### 4-1. 회원가입 완료
- `POST /v1/auth/signup/complete`  (헤더 `X-Signup-Token`)
- Request (`SignupCompleteRequest` record):
  ```
  // 보호자
  nickname, password, gender, ageGroup, walkStyles(List),
  matchGender(nullable), matchAgeFrom, matchAgeTo,
  // 강아지 (AI 결과를 클라가 확정/편집해서 전달)
  petName, breed, personalityTags(List), caution, introText
  ```
  (avatarImageUrl/originalImageUrl은 **세션에서** 가져온다 — 클라 신뢰 X)
- 처리 (AuthCommandService, `@Transactional`):
  1. signup 세션 검증
  2. 닉네임 중복 재검증 → `NICKNAME_DUPLICATED` (DB unique 제약 + 예외 매핑으로 동시가입 경합 방어)
  3. password 인코딩(`PasswordEncoder`)
  4. `User` 저장 → 세션의 이미지 URL + 요청 정보로 `Pet` 저장 (연관관계 연결)
  5. signup 세션/관련 ai-job Redis 삭제
  6. `userId` 반환
- Response: `data { userId }`  (CUD는 Id만, SuccessStatus.CREATE_USER_SUCCESS)
- `@Valid`로 요청 검증 (nickname/password 형식 등).

### 4-2. 로그인
- `POST /v1/auth/login`
- Request: `{ nickname, password }`
- 처리: 닉네임으로 조회 → 없거나 비번 불일치 시 **동일하게 `LOGIN_FAILED`** (계정 존재 여부 노출 금지) → JWT(access) 발급
- Response: `data { accessToken, userId }` (refresh 토큰 운영 시 함께)

---

## Task 5 — 프로필 조회/수정 & 계정 삭제 (인증 필요)

여기부터는 JWT 인증된 사용자 기준(`/v1/user/...`).

| 기능 | 메서드 | 경로 | 비고 |
|---|---|---|---|
| 보호자 프로필 조회 | GET | `/v1/user/profile/human` | QueryService |
| 보호자 프로필 수정 | PATCH | `/v1/user/profile/human` | 변경 필드만, `~Id` 반환 |
| 강아지 프로필 조회 | GET | `/v1/user/profile/dog` | QueryService |
| 강아지 프로필 수정 | PATCH | `/v1/user/profile/dog` | `petId` 반환 |
| 계정 삭제 | DELETE | `/v1/user` | 본인 User+Pet 삭제 |

- 인증 사용자 식별은 `@AuthenticationPrincipal` 등으로 `userId` 추출(컨벤션의 `/me` 관례에 맞춰 경로엔 id를 노출하지 않음).
- 수정은 PATCH(부분 수정). `@Setter` 대신 엔티티에 의도가 드러나는 변경 메서드(`updateProfile(...)`)를 둔다.

---

## 부록 A — Claude Code에 넘기는 방법 (요약)

1. **이 `tasks.md`와 컨벤션 문서를 레포에 둔다.** 컨벤션은 `CLAUDE.md`(루트)로 옮겨두면 매번 안 붙여도 자동 반영된다.
2. **Task 하나씩** 지시한다. 예: *"tasks.md의 Task 2를 구현해줘. 컨벤션은 CLAUDE.md를 따르고, Task 0/1에서 만든 클래스를 재사용해."*
3. Task가 끝나면 컴파일/기동 확인 후 다음 Task로.
4. 엔드포인트 경로는 이 문서에 박힌 것을 **그대로** 쓰게 하고, 임의 생성 금지를 명시한다.

## 부록 B — 미정/확인 필요 항목
- 보호자-강아지 관계: 1:1 확정인지(다견 가구 대응 여부).
- `WalkStyle` 최종 enum 목록(화면의 "+추가" 처리 방식).
- 폴링 실패 응답을 200+status로 줄지, 에러코드로 줄지(클라와 합의).
- refresh 토큰 사용 여부.
- 이미지 저장소 S3 확정 여부 / 버킷·경로 규칙.
