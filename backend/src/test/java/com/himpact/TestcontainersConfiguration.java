package com.himpact;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Testcontainers configuration — starts a real PostgreSQL container for
 * integration tests.
 * This ensures tests run against the actual database engine, not an in-memory
 * mock.
 *
 * <p>The container is declared as a static singleton so that:
 * <ul>
 *   <li>It is shared across all test classes in the same JVM (avoids repeated Docker start/stop).</li>
 *   <li>Testcontainers registers a JVM shutdown hook to stop it, eliminating the resource leak.</li>
 * </ul>
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    /**
     * Shared, eagerly-started container instance.
     * Static ensures one container per test-suite JVM run.
     * Testcontainers automatically closes it via its registered shutdown hook.
     */
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                    .withDatabaseName("HIMPACT_DB")
                    .withUsername("himpact_test")
                    .withPassword("hamdy");

    static {
        POSTGRES_CONTAINER.start();
    }

    @Bean
    PostgreSQLContainer<?> postgresContainer() {
        return POSTGRES_CONTAINER;
    }
}
