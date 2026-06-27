package com.pawmesh.backend.domain.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pawmesh.backend.domain.auth.enums.AiJobStatus;
import com.pawmesh.backend.domain.auth.session.AiJob;
import com.pawmesh.backend.domain.auth.session.AiPhotoResult;
import java.util.List;

// 폴링 응답. DONE 일 때만 결과 필드를 채우고, 나머지는 status 만 내려보낸다.
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AiPhotoJobStatusResponse(
        String status,
        String avatarImageUrl,
        String breed,
        List<String> personalityTags,
        String introText
) {

    public static AiPhotoJobStatusResponse from(AiJob job) {
        AiPhotoResult result = job.result();
        if (job.status() == AiJobStatus.DONE && result != null) {
            return new AiPhotoJobStatusResponse(
                    AiJobStatus.DONE.name(),
                    result.avatarImageUrl(),
                    result.breed(),
                    result.personalityTags(),
                    result.introText()
            );
        }
        return new AiPhotoJobStatusResponse(job.status().name(), null, null, null, null);
    }
}
