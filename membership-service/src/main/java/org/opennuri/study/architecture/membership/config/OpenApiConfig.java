package org.opennuri.study.architecture.membership.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public OpenAPI membershipOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Membership Service API")
                        .description("멤버십 관리 서비스 API - 회원 등록, 조회, 수정, 삭제 기능 제공")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("API Support Team")
                                .email("support@opennuri.org")
                                .url("https://opennuri.org"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("로컬 개발 서버"),
                        new Server()
                                .url("https://api-dev.opennuri.org")
                                .description("개발 환경"),
                        new Server()
                                .url("https://api.opennuri.org")
                                .description("운영 환경")
                ))
                .components(new Components()
                        .addSchemas("ErrorResponse", createErrorResponseSchema())
                        .addResponses("BadRequest", createBadRequestResponse())
                        .addResponses("NotFound", createNotFoundResponse())
                        .addResponses("InternalServerError", createInternalServerErrorResponse())
                );
    }

    private Schema<?> createErrorResponseSchema() {
        return new Schema<>()
                .type("object")
                .addProperty("timestamp", new StringSchema().example("2025-12-19T10:30:00"))
                .addProperty("status", new Schema<>().type("integer").example(400))
                .addProperty("error", new StringSchema().example("Bad Request"))
                .addProperty("message", new StringSchema().example("유효성 검증 실패"))
                .addProperty("path", new StringSchema().example("/memberships"))
                .addProperty("errors", new Schema<>().type("array"));
    }

    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
                .description("잘못된 요청 - 유효성 검증 실패")
                .content(new io.swagger.v3.oas.models.media.Content()
                        .addMediaType("application/json",
                                new io.swagger.v3.oas.models.media.MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))));
    }

    private ApiResponse createNotFoundResponse() {
        return new ApiResponse()
                .description("리소스를 찾을 수 없음");
    }

    private ApiResponse createInternalServerErrorResponse() {
        return new ApiResponse()
                .description("서버 내부 오류");
    }
}
