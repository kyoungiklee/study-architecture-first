package org.opennuri.study.architecture.query.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI queryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Query / Aggregation Service API")
                        .description("여러 마이크로서비스의 데이터를 조회 및 집계하여 제공하는 서비스입니다.")
                        .version("1.0.0"));
    }
}
