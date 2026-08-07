package com.talnova.tesp.aiservice;

import com.talnova.tesp.aiservice.pii.PiiSanitizerEngineImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PiiSanitizerEngineTest {

    private PiiSanitizerEngineImpl sanitizerEngine;

    @BeforeEach
    void setUp() {
        sanitizerEngine = new PiiSanitizerEngineImpl();
    }

    @Test
    @DisplayName("TC-AI-001 / TC-AI-201-01: Scrub names, emails, phone numbers, and employee IDs from text comment")
    void testSanitizeTextPiiMasking() {
        String rawComment = "Please contact John Doe at john.doe@email.com or phone +1-555-0199 regarding EMP99201.";

        String sanitized = sanitizerEngine.sanitizeText(rawComment);

        assertNotNull(sanitized);
        assertFalse(sanitized.contains("john.doe@email.com"), "Raw email must be scrubbed per BR-AI-001");
        assertFalse(sanitized.contains("+1-555-0199"), "Raw phone number must be scrubbed per BR-AI-001");
        assertFalse(sanitized.contains("John Doe"), "Raw employee name must be scrubbed per BR-AI-001");
        assertTrue(sanitized.contains("[MASKED_NAME]"));
        assertTrue(sanitized.contains("[MASKED_EMAIL]"));
        assertTrue(sanitized.contains("[MASKED_PHONE]"));
        assertTrue(sanitized.contains("[MASKED_EMP_ID]"));
    }

    @Test
    @DisplayName("TC-AI-201-02: Check containsUnmaskedPii detection logic")
    void testContainsUnmaskedPii() {
        assertTrue(sanitizerEngine.containsUnmaskedPii("Send details to alice@test.org"));
        assertFalse(sanitizerEngine.containsUnmaskedPii("Send details to [MASKED_EMAIL]"));
    }
}
