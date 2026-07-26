package com.himpact.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger documentation configuration.
 * Available at: /swagger-ui/index.html
 * JSON spec at: /v3/api-docs
 *
 * See: project-index/07_API_Specification.md
 */
@Configuration
public class OpenApiConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI himpactOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HImpact Digital Event Automation API")
                        .description("""
                                REST API for the HImpact Digital Event Automation Platform.
                                
                                All endpoints require Bearer JWT authentication unless marked as public.
                                Obtain a token via POST /api/v1/auth/google.
                                
                                Base URL: /api/v1
                                """)
                        .version("0.2.0")
                        .contact(new Contact()
                                .name("HImpact")
                                .email("support@himpact.app"))
                        .license(new License()
                                .name("Private — HImpact © 2026")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste the JWT access token received from /api/v1/auth/google")));
    }
}
