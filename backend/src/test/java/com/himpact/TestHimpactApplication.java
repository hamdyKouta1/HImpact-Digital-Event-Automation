package com.himpact;

import org.springframework.boot.SpringApplication;

/**
 * Test application entry point for running the application with the test profile.
 * Used with Testcontainers to spin up a real PostgreSQL container for integration tests.
 */
public class TestHimpactApplication {

    public static void main(String[] args) {
        SpringApplication.from(HimpactApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
