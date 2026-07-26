package com.himpact.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Centralised application configuration properties.
 * All values are loaded from application.yml and overridden by environment variables.
 * Never hardcode values here — use application.yml with ${ENV_VAR:default} placeholders.
 *
 * See: project-index/10_Deployment_DevOps.md — Secrets Management
 */
@Configuration
@ConfigurationProperties(prefix = "himpact")
@Data
public class AppProperties {

    private Security security = new Security();
    private Google google = new Google();

    @Data
    public static class Security {
        private Jwt jwt = new Jwt();
        private Cors cors = new Cors();

        @Data
        public static class Jwt {
            private String secret;
            private long expirationMs;
            private long refreshExpirationMs;
        }

        @Data
        public static class Cors {
            private List<String> allowedOrigins = List.of("http://localhost:5173");
        }
    }

    @Data
    public static class Google {
        private String clientId;
        private String clientSecret;
        private Drive drive = new Drive();

        @Data
        public static class Drive {
            private String applicationName;
            private String credentialsPath;
        }
    }
}
