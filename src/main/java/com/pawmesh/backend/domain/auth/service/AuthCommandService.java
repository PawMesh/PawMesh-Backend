package com.pawmesh.backend.domain.auth.service;

import com.pawmesh.backend.common.exception.GeneralException;
import com.pawmesh.backend.common.jwt.JwtProvider;
import com.pawmesh.backend.common.status.error.ErrorStatus;
import com.pawmesh.backend.common.storage.ImageStorage;
import com.pawmesh.backend.domain.auth.converter.AuthConverter;
import com.pawmesh.backend.domain.auth.dto.request.AiPhotoGenerateRequest;
import com.pawmesh.backend.domain.auth.dto.request.LoginRequest;
import com.pawmesh.backend.domain.auth.dto.request.ReissueRequest;
import com.pawmesh.backend.domain.auth.dto.request.SignupCompleteRequest;
import com.pawmesh.backend.domain.auth.dto.response.AiPhotoJobResponse;
import com.pawmesh.backend.domain.auth.dto.response.CreateUserResponse;
import com.pawmesh.backend.domain.auth.dto.response.LoginResponse;
import com.pawmesh.backend.domain.auth.dto.response.UploadPetImageResponse;
import com.pawmesh.backend.domain.auth.session.AiJob;
import com.pawmesh.backend.domain.auth.session.AiPhotoResult;
import com.pawmesh.backend.domain.auth.session.SignupSession;
import com.pawmesh.backend.domain.user.entity.User;
import com.pawmesh.backend.domain.dog.repository.PetRepository;
import com.pawmesh.backend.domain.user.repository.UserRepository;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthCommandService {

    private static final String PET_IMAGE_DIR = "pet-images/original";
    private static final long MAX_IMAGE_BYTES = 25L * 1024 * 1024; // 25MB (OpenAI edits 제약)
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/png", "image/jpeg", "image/jpg");

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final AuthConverter authConverter;
    private final ImageStorage imageStorage;
    private final SignupSessionService signupSessionService;
    private final AiJobService aiJobService;
    private final AiPhotoGenerationService aiPhotoGenerationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 강아지 원본 사진을 업로드하고 signup 세션/토큰을 발급한다.
    public UploadPetImageResponse uploadPetImage(MultipartFile image) {
        // 이미지 형식/용량 검증
        validateImage(image);
        // 원본 업로드 → key 획득
        String originalImageKey = imageStorage.upload(image, PET_IMAGE_DIR);
        // signup 세션 생성 (TTL 30분)
        String signupToken = signupSessionService.create(SignupSession.of(originalImageKey));
        return UploadPetImageResponse.of(signupToken, imageStorage.getUrl(originalImageKey));
    }

    // AI 생성을 비동기로 시작하고 jobId 를 즉시 반환한다.
    public AiPhotoJobResponse startAiPhotoGeneration(String signupToken, AiPhotoGenerateRequest request) {
        // signup 세션 검증 → 원본 이미지 key 확보
        SignupSession session = signupSessionService.getOrThrow(signupToken);
        // jobId 발급 + 작업 상태 PROCESSING + 세션에 jobId 기록
        String jobId = UUID.randomUUID().toString();
        aiJobService.save(jobId, AiJob.processing());
        signupSessionService.update(signupToken, session.withJobId(jobId));
        // 비동기 실행 (즉시 반환)
        String petName = (request != null) ? request.petName() : null;
        aiPhotoGenerationService.generateAsync(signupToken, jobId, session.originalImageKey(), petName);
        return AiPhotoJobResponse.of(jobId);
    }

    // 회원가입 완료: 토큰 검증 → User + Pet 생성 → 세션/ai-job 정리 → userId 반환.
    public CreateUserResponse completeSignup(String signupToken, SignupCompleteRequest request) {
        SignupSession session = signupSessionService.getOrThrow(signupToken);
        // 닉네임 중복 재검증
        if (userRepository.existsByNickname(request.nickname())) {
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATED);
        }
        // password 인코딩 후 User 저장 (동시가입 경합은 unique 제약 위반으로 방어)
        String encodedPassword = passwordEncoder.encode(request.password());
        User user = saveUser(request, encodedPassword);
        // 세션의 이미지 URL + 요청 정보로 Pet 저장
        String originalImageUrl = imageStorage.getUrl(session.originalImageKey());
        String avatarImageUrl = avatarImageUrlOf(session);
        petRepository.save(authConverter.toPet(request, user, originalImageUrl, avatarImageUrl));
        // signup 세션 / 관련 ai-job 정리
        signupSessionService.delete(signupToken);
        if (session.jobId() != null) {
            aiJobService.delete(session.jobId());
        }
        return CreateUserResponse.of(user.getUserId());
    }

    // 로그인: 닉네임/비번 검증 → access + refresh 발급.
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByNickname(request.nickname())
                .orElseThrow(() -> new GeneralException(ErrorStatus.LOGIN_FAILED));
        // 계정 존재 여부 노출 금지 — 불일치도 동일하게 LOGIN_FAILED
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new GeneralException(ErrorStatus.LOGIN_FAILED);
        }
        return issueTokens(user.getUserId());
    }

    // 토큰 재발급: refresh 토큰 검증(저장값 일치) → 새 access + refresh 발급(회전).
    public LoginResponse reissue(ReissueRequest request) {
        String refreshToken = request.refreshToken();
        if (!jwtProvider.validate(refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
        Long userId = jwtProvider.getUserId(refreshToken);
        if (!refreshTokenService.matches(userId, refreshToken)) {
            throw new GeneralException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
        return issueTokens(userId);
    }

    // User 저장 (닉네임 unique 위반 → NICKNAME_DUPLICATED 매핑)
    private User saveUser(SignupCompleteRequest request, String encodedPassword) {
        try {
            return userRepository.saveAndFlush(authConverter.toUser(request, encodedPassword));
        } catch (DataIntegrityViolationException e) {
            throw new GeneralException(ErrorStatus.NICKNAME_DUPLICATED);
        }
    }

    // access + refresh 발급 후 refresh 저장
    private LoginResponse issueTokens(Long userId) {
        String accessToken = jwtProvider.createAccessToken(userId);
        String refreshToken = jwtProvider.createRefreshToken(userId);
        refreshTokenService.save(userId, refreshToken);
        return LoginResponse.of(accessToken, refreshToken, userId);
    }

    private String avatarImageUrlOf(SignupSession session) {
        AiPhotoResult result = session.aiResult();
        return (result != null) ? result.avatarImageUrl() : null;
    }

    // PNG/JPG, 25MB 미만 검증
    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new GeneralException(ErrorStatus.INVALID_PET_IMAGE);
        }
        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new GeneralException(ErrorStatus.INVALID_PET_IMAGE);
        }
        if (image.getSize() >= MAX_IMAGE_BYTES) {
            throw new GeneralException(ErrorStatus.INVALID_PET_IMAGE);
        }
    }
}
