package com.talnova.tesp.configservice;

import com.talnova.tesp.configservice.dto.ContrastValidationResponseDTO;
import com.talnova.tesp.configservice.validation.WcagColorAccessibilityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WcagAccessibilityValidatorTest {

    private WcagColorAccessibilityValidator validator;

    @BeforeEach
    void setUp() {
        validator = new WcagColorAccessibilityValidator();
    }

    @Test
    @DisplayName("TC-CFG-501-A: White text (#FFFFFF) on Black background (#000000) achieves 21.0 contrast ratio and AAA level")
    void testWhiteOnBlackHighContrast() {
        ContrastValidationResponseDTO result = validator.validateColorContrast("#FFFFFF", "#000000");

        assertNotNull(result);
        assertTrue(result.isPassed());
        assertEquals(21.0, result.getContrastRatio());
        assertEquals("AAA", result.getWcagLevel());
    }

    @Test
    @DisplayName("TC-CFG-501-B: Yellow text (#FFFF00) on White background (#FFFFFF) fails WCAG 2.1 AA with ratio ~1.07")
    void testYellowOnWhiteLowContrastFails() {
        ContrastValidationResponseDTO result = validator.validateColorContrast("#FFFF00", "#FFFFFF");

        assertNotNull(result);
        assertFalse(result.isPassed());
        assertEquals(1.07, result.getContrastRatio());
        assertEquals("FAIL", result.getWcagLevel());
        assertTrue(result.getRecommendation().contains("fails WCAG 2.1 AA requirement"));
    }

    @Test
    @DisplayName("TC-CFG-501-C: Navy primary (#1E3A8A) on White background (#FFFFFF) achieves AA compliance (> 4.5:1)")
    void testNavyOnWhitePassesAA() {
        ContrastValidationResponseDTO result = validator.validateColorContrast("#1E3A8A", "#FFFFFF");

        assertNotNull(result);
        assertTrue(result.isPassed());
        assertTrue(result.getContrastRatio() >= 4.5);
    }
}
