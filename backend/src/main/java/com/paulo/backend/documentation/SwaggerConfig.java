package com.paulo.backend.documentation;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;

public class SwaggerConfig {

    @Bean
    public OpenAPI auditFlowOpenApi(){
        return new OpenAPI()
                .info(new Info()
                        .title("AuditFlow API")
                        .description(
                                "API para gestão de auditorias,"
                                + "apontamentos e planos de ação."
                        )
                        .version("V1"));

    }
}
