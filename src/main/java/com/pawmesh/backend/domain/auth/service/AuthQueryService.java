package com.pawmesh.backend.domain.auth.service;

import com.pawmesh.backend.domain.auth.dto.response.AiPhotoJobStatusResponse;
import com.pawmesh.backend.domain.auth.dto.response.CheckNicknameResponse;
import com.pawmesh.backend.domain.auth.session.AiJob;
import com.pawmesh.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthQueryService {

    /** UserRepository **/
    private final UserRepository userRepository;

    private final SignupSessionService signupSessionService;
    private final AiJobService aiJobService;

    // 닉네임 사용 가능 여부를 확인한다. (사용 중이면 available=false)
    public CheckNicknameResponse checkNickname(String nickname) {
        boolean available = !userRepository.existsByNickname(nickname);
        return CheckNicknameResponse.of(available);
    }

    // AI 생성 상태를 조회한다. (세션 검증 후 ai-job 조회)
    public AiPhotoJobStatusResponse getAiPhotoJob(String signupToken, String jobId) {
        signupSessionService.getOrThrow(signupToken);
        AiJob job = aiJobService.getOrThrow(jobId);
        return AiPhotoJobStatusResponse.from(job);
    }
}
