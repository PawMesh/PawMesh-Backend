package com.pawmesh.backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI pawMeshOpenAPI() {
        // 전역 SecurityRequirement 는 걸지 않는다. 인증이 필요한 컨트롤러에만
        // @SecurityRequirement(name = "BearerAuth") 로 자물쇠를 지정한다.
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, bearerAuthScheme()));
    }

    private Info apiInfo() {
        return new Info()
                .title("PawMesh Backend API")
                .description("PawMesh - 산책 기반 반려견 소셜 앱 백엔드 API 문서")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"));
    }

    private SecurityScheme bearerAuthScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }
}
