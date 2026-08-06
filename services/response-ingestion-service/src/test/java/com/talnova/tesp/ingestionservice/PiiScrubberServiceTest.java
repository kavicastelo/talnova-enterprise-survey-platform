package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.service.PiiScrubberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PiiScrubberServiceTest {

    private PiiScrubberServiceImpl piiScrubberService;

    @BeforeEach
    void setUp() {
        piiScrubberService = new PiiScrubberServiceImpl();
    }

    @Test
    @DisplayName("TC-INT-501-01: Sanitize open-text response scrubbing email and phone number PII strings")
    void testSanitizeOpenTextPii() {
        String rawText = "Please contact me at john@aitkenspence.lk or call +94771234567 regarding the HR feedback.";

        String sanitized = piiScrubberService.sanitizeOpenText(rawText);

        assertNotNull(sanitized);
        assertTrue(sanitized.contains("[REDACTED_EMAIL]"));
        assertTrue(sanitized.contains("[REDACTED_PHONE]"));
        assertFalse(sanitized.contains("john@aitkenspence.lk"));
        assertFalse(sanitized.contains("+94771234567"));
    }
}
