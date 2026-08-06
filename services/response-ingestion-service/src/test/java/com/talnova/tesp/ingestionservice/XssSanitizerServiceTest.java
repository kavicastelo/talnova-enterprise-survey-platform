package com.talnova.tesp.ingestionservice;

import com.talnova.tesp.ingestionservice.service.XssSanitizerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XssSanitizerServiceTest {

    private XssSanitizerServiceImpl xssSanitizerService;

    @BeforeEach
    void setUp() {
        xssSanitizerService = new XssSanitizerServiceImpl();
    }

    @Test
    @DisplayName("TC-INT-801-01: Sanitize text input stripping script tags and malicious HTML entities")
    void testSanitizeTextScriptTags() {
        String rawInput = "<script>alert('xss')</script>Great work environment!";

        String sanitized = xssSanitizerService.sanitizeText(rawInput);

        assertNotNull(sanitized);
        assertFalse(sanitized.contains("<script>"));
        assertFalse(sanitized.contains("</script>"));
        assertTrue(sanitized.contains("Great work environment!"));
    }
}
