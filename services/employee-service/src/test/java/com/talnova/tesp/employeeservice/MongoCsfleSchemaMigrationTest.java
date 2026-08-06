package com.talnova.tesp.employeeservice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class MongoCsfleSchemaMigrationTest {

    private static final String MIGRATION_FILE = "db/migration/v1_003_create_employees_csfle.js";

    @Test
    @DisplayName("TC-EMP-101-A: Verify MongoDB CSFLE schema migration script exists and defines required encrypted PII fields")
    void testCsfleSchemaMigrationScriptValidation() throws Exception {
        ClassPathResource resource = new ClassPathResource(MIGRATION_FILE);
        assertTrue(resource.exists(), "Migration script v1_003_create_employees_csfle.js must exist on classpath");

        String content;
        try (InputStream inputStream = resource.getInputStream()) {
            content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertNotNull(content);
        assertTrue(content.contains("createCollection(\"employees\""), "Script must target 'employees' collection");
        assertTrue(content.contains("AEAD_AES_256_CBC_HMAC_SHA_512-Deterministic"), "Script must specify deterministic CSFLE encryption for exact email/phone queries");
        assertTrue(content.contains("AEAD_AES_256_CBC_HMAC_SHA_512-Random"), "Script must specify randomized CSFLE encryption for fullName");
        assertTrue(content.contains("\"projectId\""), "Script must contain projectId field");
        assertTrue(content.contains("\"employeeId\""), "Script must contain employeeId field");
        assertTrue(content.contains("\"nodeId\""), "Script must contain nodeId field");
        assertTrue(content.contains("\"status\""), "Script must contain status enum field");
        assertTrue(content.contains("\"isDeleted\""), "Script must contain isDeleted boolean field");
    }
}
