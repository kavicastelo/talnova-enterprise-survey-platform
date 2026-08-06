package com.talnova.tesp.employeeservice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class MongoCsfleSchemaMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MongoCsfleSchemaMigrationRunner.class);
    private static final String MIGRATION_FILE = "db/migration/v1_003_create_employees_csfle.js";

    @Override
    public void run(String... args) throws Exception {
        try {
            ClassPathResource resource = new ClassPathResource(MIGRATION_FILE);
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    String scriptContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    log.info("Successfully loaded MongoDB Atlas CSFLE schema migration script: {} ({} bytes)",
                            MIGRATION_FILE, scriptContent.length());
                }
            } else {
                log.warn("MongoDB CSFLE schema migration script not found at classpath: {}", MIGRATION_FILE);
            }
        } catch (Exception ex) {
            log.error("Failed to initialize MongoDB CSFLE schema migration script {}: {}", MIGRATION_FILE, ex.getMessage(), ex);
        }
    }
}
