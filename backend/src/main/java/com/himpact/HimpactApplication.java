package com.himpact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * HImpact Digital Event Automation Platform — Main Application Entry Point.
 *
 * <p>Architecture: Layered (Controller → Service → Repository → Database)
 * Documentation: project-index/05_Software_Architecture.md
 */
@SpringBootApplication
@EnableScheduling
public class HimpactApplication {

    public static void main(String[] args) {
        SpringApplication.run(HimpactApplication.class, args);
    }
}
